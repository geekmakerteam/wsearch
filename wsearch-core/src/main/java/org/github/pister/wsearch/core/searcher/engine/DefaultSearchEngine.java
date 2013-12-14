package org.github.pister.wsearch.core.searcher.engine;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.github.pister.wsearch.core.doc.DocumentTransformUtil;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.log.Logger;
import org.github.pister.wsearch.core.log.LoggerFactory;
import org.github.pister.wsearch.core.schema.DataDirectory;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.searcher.SearchEngine;
import org.github.pister.wsearch.core.searcher.query.FieldSort;
import org.github.pister.wsearch.core.searcher.query.OutputDocument;
import org.github.pister.wsearch.core.searcher.query.SearchQuery;
import org.github.pister.wsearch.core.searcher.response.AddResponse;
import org.github.pister.wsearch.core.searcher.response.DeleteResponse;
import org.github.pister.wsearch.core.searcher.response.OperationResponse;
import org.github.pister.wsearch.core.searcher.response.QueryResponse;
import org.github.pister.wsearch.core.searcher.response.ResultCodes;
import org.github.pister.wsearch.core.util.CloseUtil;
import org.github.pister.wsearch.core.util.CollectionUtil;
import org.github.pister.wsearch.core.util.LuceneConfig;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 上午11:29
 */
public class DefaultSearchEngine implements SearchEngine {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSearchEngine.class);
    private DataDirectory dataDirectory;
    private Schema schema;
    private volatile IndexWriter indexWriter;
    private volatile IndexSearcher indexSearcher;
    private Directory directory;
    private int defaultMergeSize = 5;
    private AtomicBoolean inited = new AtomicBoolean(false);
    private AtomicBoolean opened = new AtomicBoolean(false);
    private AtomicInteger updateCount = new AtomicInteger(0);
    private Lock reopenLock = new ReentrantLock();

    public DefaultSearchEngine(DataDirectory dataDirectory, Schema schema) {
        this.dataDirectory = dataDirectory;
        this.schema = schema;
        init();
    }

    @Override
    public DataDirectory getDataDirectory() {
        return dataDirectory;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    private void init() {
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        try {
            directory = dataDirectory.openDirectory();
            open(directory);
            opened.set(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void open(Directory directory) throws IOException {
        IndexWriter newIndexWriter = null;
        IndexReader newIndexReader = null;
        IndexSearcher newIndexSearcher = null;
        if (logger.isDebugEnabled()) {
            logger.debug("opening directory...");
        }
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(LuceneConfig.LUCENE_VERSION, schema.getAnalyzer());
            newIndexWriter = new IndexWriter(directory, indexWriterConfig);
            newIndexReader = IndexReader.open(newIndexWriter, true);
            newIndexSearcher = new IndexSearcher(newIndexReader);

            synchronized (this) {
                IndexWriter oldIndexWriter = this.indexWriter;
                IndexSearcher oldIndexSearcher = this.indexSearcher;

                this.indexWriter = newIndexWriter;
                this.indexSearcher = newIndexSearcher;

                CloseUtil.close(oldIndexSearcher);
                CloseUtil.close(oldIndexWriter);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("open directory finish.");
            }
        } catch (IOException e) {
            logger.error("open directory error", e);
            CloseUtil.close(newIndexSearcher);
            CloseUtil.close(newIndexReader);
            CloseUtil.close(newIndexWriter);
            throw e;
        }

    }

    protected void reopenSearcher() throws IOException {
        try {
            reopenLock.lock();
            if (updateCount.get() > 0) {
                logger.info("proccessing reopen...");
                IndexReader indexReader = IndexReader.openIfChanged(indexSearcher.getIndexReader());
                IndexSearcher newIndexSearcher = new IndexSearcher(indexReader);
                // please do not indexSearcher.close();
                indexSearcher = newIndexSearcher;
                updateCount.set(0);
                logger.info("proccess reopen finish.");
            } else {
                logger.info("no thing to reopen");
            }
        } finally {
            reopenLock.unlock();
        }
    }

    @Override
    public OperationResponse reopen() {
        try {
            reopenSearcher();
        } catch (IOException e) {
            logger.error("repen error", e);
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        }
        return new OperationResponse();
    }

    @Override
    public void close() {
        CloseUtil.close(indexSearcher);
        CloseUtil.close(indexWriter);
        CloseUtil.close(directory);
        opened.set(false);
        if (logger.isDebugEnabled()) {
            logger.debug("search engine has been closed.");
        }
    }

    @Override
    public AddResponse add(Collection<InputDocument> inputDocuments) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("adding documents...");
            }
            for (InputDocument inputDocument : inputDocuments) {
                assertIdExist(inputDocument);
            }
            for (Document document : DocumentTransformUtil.toLuceneDocuments(inputDocuments, schema)) {
                indexWriter.updateDocument(new Term(schema.getIdName(), document.getFieldable(schema.getIdName()).stringValue()), document, schema.getAnalyzer());
            }
            updateCount.addAndGet(inputDocuments.size());
            if (logger.isDebugEnabled()) {
                logger.debug("add documents finish.");
            }
        } catch (Exception e) {
            logger.error("add documents error", e);
            return new AddResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        }
        return new AddResponse();
    }

    @Override
    public AddResponse add(InputDocument inputDocument) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("adding document...");
            }
            assertIdExist(inputDocument);
            Document document = DocumentTransformUtil.toLuceneDocument(inputDocument, schema);
            indexWriter.updateDocument(new Term(schema.getIdName(), document.getFieldable(schema.getIdName()).stringValue()), document, schema.getAnalyzer());
            updateCount.incrementAndGet();
            if (logger.isDebugEnabled()) {
                logger.debug("add document finish.");
            }
        } catch (IOException e) {
            return new AddResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        }
        return new AddResponse();
    }

    private void assertIdExist(InputDocument inputDocument) {
        if (inputDocument.getFields().get(schema.getIdName()) == null) {
            throw new IllegalArgumentException("input document's id:" + schema.getIdName() + " must not be empty!");
        }
    }

    @Override
    public OperationResponse commit() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("commiting...");
            }
            indexWriter.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("commit finish.");
            }
            reopenSearcher();
        } catch (IOException e) {
            logger.error("commit error", e);
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        } catch (OutOfMemoryError e) {
            CloseUtil.close(indexWriter);
            logger.error("error of OOM", e);
            // TODO reopen writer?
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);

        }
        return new OperationResponse();
    }

    @Override
    public OperationResponse optimize() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("optimizing...");
            }
            indexWriter.forceMerge(defaultMergeSize);
            if (logger.isDebugEnabled()) {
                logger.debug("optimize finish.");
            }
            reopenSearcher();
        } catch (IOException e) {
            logger.error("optimize error", e);
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        } catch (OutOfMemoryError e) {
            CloseUtil.close(indexWriter);
            logger.error("error of OOM", e);
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);

        }
        return new OperationResponse();
    }

    public OperationResponse commitAndOptimize() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("commiting...");
            }
            indexWriter.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("commit finish.");
            }
            if (logger.isDebugEnabled()) {
                logger.debug("optimizing...");
            }
            indexWriter.forceMerge(defaultMergeSize);
            if (logger.isDebugEnabled()) {
                logger.debug("optimize finish.");
            }
            reopenSearcher();
        } catch (IOException e) {
            logger.error("optimize error", e);
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        } catch (OutOfMemoryError e) {
            CloseUtil.close(indexWriter);
            logger.error("error of OOM", e);
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);

        }
        return new OperationResponse();
    }

    @Override
    public OperationResponse rollback() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("rollbacking...");
            }
            indexWriter.rollback();
            if (logger.isDebugEnabled()) {
                logger.debug("rollback finish.");
            }
        } catch (IOException e) {
            logger.error("rollback error", e);
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        }
        return new OperationResponse();
    }

    @Override
    public DeleteResponse deleteByIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return new DeleteResponse();
        }

        final String idName = schema.getIdName();
        Term[] terms = new Term[ids.size()];
        int index = 0;
        for (String id : ids) {
            terms[index++] = new Term(idName, id);
        }
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("deleting documents...");
            }
            indexWriter.deleteDocuments(terms);
            updateCount.addAndGet(ids.size());
            if (logger.isDebugEnabled()) {
                logger.debug("delete documents finish.");
            }
        } catch (IOException e) {
            logger.error("delete error", e);
            return new DeleteResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        }
        return new DeleteResponse();
    }

    @Override
    public DeleteResponse deleteById(String id) {
        return deleteByIds(Arrays.asList(id));
    }

    private Sort getSort(SearchQuery searchQuery) {
        List<FieldSort> fieldSorts = searchQuery.getFieldSorts();

        Sort sort;
        if (CollectionUtil.isEmpty(fieldSorts)) {
            sort = Sort.RELEVANCE;
        } else {
            sort = new Sort();
            SortField[] targetSorts = new SortField[fieldSorts.size()];
            int i = 0;
            for (FieldSort fieldSort : fieldSorts) {
                String name = fieldSort.getName();
                FieldInfo fieldInfo = schema.getFieldInfos().get(name);
                boolean orderOfDesc = (fieldSort.getOrder() == FieldSort.DESC);
                SortField sortField = new SortField(name, fieldInfo.getFieldType().getSortType(), orderOfDesc);
                targetSorts[i++] = sortField;
            }
            sort.setSort(targetSorts);
        }
        return sort;
    }

    private Filter getFilter(SearchQuery searchQuery) {
        return null;
    }

    @Override
    public QueryResponse query(SearchQuery searchQuery) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("searching query...");
            }
            long start = System.currentTimeMillis();
            QueryParser queryParser = new QueryParser(LuceneConfig.LUCENE_VERSION, schema.getDefaultSearchField(), schema.getAnalyzer());
            Query query = queryParser.parse(searchQuery.getQuery());
            int pageNo = searchQuery.getPageNo();
            int pageSize = searchQuery.getPageSize();
            int fullPageCount = pageNo * pageSize;
            int pageStartIndex = pageNo < 1 ? 0 : ((pageNo - 1) * pageSize);
            Sort sort = getSort(searchQuery);
            Filter filter = getFilter(searchQuery);
            TopFieldDocs topFieldDocs = indexSearcher.search(query, filter, fullPageCount, sort);
            ScoreDoc[] scoreDocs = topFieldDocs.scoreDocs;
            int scoreDocsLength = scoreDocs.length;
            List<OutputDocument> outputDocuments;

            if (scoreDocsLength <= pageStartIndex) {
                // 当前页没有数据了
                outputDocuments = CollectionUtil.newArrayList(0);
            } else {
                // 只获取最后一页的数据
                outputDocuments = CollectionUtil.newArrayList(scoreDocs.length - pageStartIndex);
                for (int i = pageStartIndex; i < scoreDocs.length; ++i) {
                    Document doc = indexSearcher.doc(scoreDocs[i].doc);
                    OutputDocument outputDocument = DocumentTransformUtil.toOutputDocument(doc, schema);
                    outputDocuments.add(outputDocument);
                }
            }
            QueryResponse queryResponse = new QueryResponse();

            queryResponse.setOutputDocuments(outputDocuments);
            queryResponse.setTotalHits(topFieldDocs.totalHits);

            if (logger.isDebugEnabled()) {
                logger.debug("search query finish.");
            }
            long end = System.currentTimeMillis();
            long timeEscape = end - start;
            queryResponse.setTimeEscape(timeEscape);
            return queryResponse;
        } catch (Exception e) {
            logger.error("search query error", e);
            return new QueryResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        }
    }
}

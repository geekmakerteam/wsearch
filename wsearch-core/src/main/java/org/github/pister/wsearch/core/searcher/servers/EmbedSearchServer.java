package org.github.pister.wsearch.core.searcher.servers;

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
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.github.pister.wsearch.core.doc.DocumentTransformUtil;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.log.Logger;
import org.github.pister.wsearch.core.log.LoggerFactory;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.searcher.SearchServer;
import org.github.pister.wsearch.core.searcher.query.FieldSort;
import org.github.pister.wsearch.core.searcher.query.OutputDocument;
import org.github.pister.wsearch.core.searcher.query.SearchQuery;
import org.github.pister.wsearch.core.searcher.response.AddResponse;
import org.github.pister.wsearch.core.searcher.response.DeleteResponse;
import org.github.pister.wsearch.core.searcher.response.OperationResponse;
import org.github.pister.wsearch.core.searcher.response.PongResponse;
import org.github.pister.wsearch.core.searcher.response.QueryResponse;
import org.github.pister.wsearch.core.searcher.response.ResultCodes;
import org.github.pister.wsearch.core.util.CollectionUtil;
import org.github.pister.wsearch.core.util.LuceneConfig;
import org.github.pister.wsearch.core.util.LuceneUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 上午11:29
 */
public class EmbedSearchServer implements SearchServer {

    private static final Logger logger = LoggerFactory.getLogger(EmbedSearchServer.class);

    private final int REOPEN_MIN_COUNT = 1000;
    private final int REOPEN_WAIT_TIMEOUT = 2 * 60;
    private Schema schema;
    private volatile IndexWriter indexWriter;
    private volatile IndexReader indexReader;
    private volatile IndexSearcher indexSearcher;
    private Directory directory;
    private int defaultMergeSize = 5;
    private AtomicBoolean inited = new AtomicBoolean(false);
    private AtomicBoolean opened = new AtomicBoolean(false);
    private AtomicInteger updateCount = new AtomicInteger(0);
    private Lock reopenLock = new ReentrantLock();
    private Condition reopenCondition = reopenLock.newCondition();
    private Thread reopenThread = new Thread() {
        @Override
        public void run() {
            while (opened.get()) {
                try {
                    if (needReopen()) {
                        indexReader = IndexReader.openIfChanged(indexReader);
                        updateCount.set(0);
                    }
                } catch (IOException e) {
                    logger.error("proccess reopen error", e);
                }
            }
        }

        private boolean needReopen() {
            try {
                reopenLock.lock();
                boolean timeout = reopenCondition.await(REOPEN_WAIT_TIMEOUT, TimeUnit.SECONDS);
                if (!timeout) {
                    return true;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                reopenLock.unlock();
            }
            if (updateCount.get() > REOPEN_MIN_COUNT) {
                return true;
            }
            return false;
        }
    };

    public EmbedSearchServer(Schema schema) {
        this.schema = schema;
    }

    public void init() {
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        try {
            directory = FSDirectory.open(schema.getSchemaMeta().getDataPath());
            open(directory);
            opened.set(true);
            reopenThread.start();
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
                IndexReader oldIndexReader = this.indexReader;
                IndexSearcher oldIndexSearcher = this.indexSearcher;

                this.indexReader = newIndexReader;
                this.indexWriter = newIndexWriter;
                this.indexSearcher = newIndexSearcher;

                LuceneUtil.close(oldIndexSearcher);
                LuceneUtil.close(oldIndexReader);
                LuceneUtil.close(oldIndexWriter);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("open directory finish.");
            }
        } catch (IOException e) {
            logger.error("open directory error", e);
            LuceneUtil.close(newIndexSearcher);
            LuceneUtil.close(newIndexReader);
            LuceneUtil.close(newIndexWriter);
            throw e;
        }

    }

    public void close() {
        LuceneUtil.close(indexSearcher);
        LuceneUtil.close(indexReader);
        LuceneUtil.close(indexWriter);
        opened.set(false);
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
          //  indexWriter.addDocuments(DocumentTransformUtil.toLuceneDocuments(inputDocuments, schema), schema.getAnalyzer());
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
        } catch (IOException e) {
            logger.error("commit error", e);
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        } catch (OutOfMemoryError e) {
            LuceneUtil.close(indexWriter);
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
        } catch (IOException e) {
            logger.error("optimize error", e);
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        } catch (OutOfMemoryError e) {
            LuceneUtil.close(indexWriter);
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

    @Override
    public PongResponse ping() {
        return new PongResponse();
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

    @Override
    public QueryResponse query(SearchQuery searchQuery) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("searching query...");
            }
            QueryParser queryParser = new QueryParser(LuceneConfig.LUCENE_VERSION, schema.getDefaultSearchField(), schema.getAnalyzer());
            Query query = queryParser.parse(searchQuery.getQuery());
            Filter filter = null;
            int pageNo = searchQuery.getPageNo();
            int pageSize = searchQuery.getPageSize();
            int fullPageCount = pageNo * pageSize;
            int pageStartIndex = pageNo < 1 ? 0 : ((pageNo - 1) * pageSize);
            Sort sort = getSort(searchQuery);
            // TODO
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
            return queryResponse;
        } catch (Exception e) {
            logger.error("search query error", e);
            return new QueryResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        }
    }
}

package org.github.pister.wsearch.core.searcher.servers;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldDocs;
import org.github.pister.wsearch.core.doc.DocumentTransformUtil;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.searcher.SearchServer;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 上午11:29
 */
public class EmbedSearchServer implements SearchServer {

    private Schema schema;
    private IndexWriter indexWriter;
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;
    private int defaultMergeSize = 5;

    public EmbedSearchServer(Schema schema) {
        this.schema = schema;
    }

    @Override
    public AddResponse add(Collection<InputDocument> inputDocuments) {
        try {
            for (InputDocument inputDocument : inputDocuments) {
                assertIdExist(inputDocument);
            }
            indexWriter.addDocuments(DocumentTransformUtil.toLuceneDocuments(inputDocuments, schema), schema.getAnalyzer());
        } catch (Exception e) {
            return new AddResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        }
        return new AddResponse();
    }

    @Override
    public AddResponse add(InputDocument inputDocument) {
        try {
            assertIdExist(inputDocument);
            indexWriter.addDocument(DocumentTransformUtil.toLuceneDocument(inputDocument, schema), schema.getAnalyzer());
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

    private void closeWriter() {
        try {
            indexWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OperationResponse commit() {
        try {
            indexWriter.commit();
        } catch (IOException e) {
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        } catch (OutOfMemoryError e) {
            closeWriter();
            // TODO reopen writer?
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);

        }
        return new OperationResponse();
    }

    @Override
    public OperationResponse optimize() {
        try {
            indexWriter.forceMerge(defaultMergeSize);
        } catch (IOException e) {
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        } catch (OutOfMemoryError e) {
            closeWriter();
            return new OperationResponse(e.getMessage(), ResultCodes.COMMON_ERROR);

        }
        return new OperationResponse();
    }

    @Override
    public OperationResponse rollback() {
        try {
            indexWriter.rollback();
        } catch (IOException e) {
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
            indexWriter.deleteDocuments(terms);
        } catch (IOException e) {
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

    @Override
    public QueryResponse query(SearchQuery searchQuery) {
        try {
            QueryParser queryParser = new QueryParser(LuceneConfig.LUCENE_VERSION, schema.getDefaultSearchField(), schema.getAnalyzer());
            Query query = queryParser.parse(searchQuery.getQuery());
            Filter filter = null;
            int pageNo = searchQuery.getPageNo();
            int pageSize = searchQuery.getPageSize();
            int fullPageCount = pageNo * pageSize;
            int pageStartIndex = pageNo < 1 ? 0 : ((pageNo - 1) * pageSize);
            Sort sort = null;
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
                    OutputDocument outputDocument = DocumentTransformUtil.toOutputDocument(doc);
                    outputDocuments.add(outputDocument);
                }
            }
            QueryResponse queryResponse = new QueryResponse();

            queryResponse.setOutputDocuments(outputDocuments);
            queryResponse.setTotalHits(topFieldDocs.totalHits);

            return queryResponse;
        } catch (Exception e) {
            return new QueryResponse(e.getMessage(), ResultCodes.COMMON_ERROR);
        }
    }
}

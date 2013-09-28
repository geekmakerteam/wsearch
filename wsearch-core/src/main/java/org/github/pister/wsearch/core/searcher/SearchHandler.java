package org.github.pister.wsearch.core.searcher;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.github.pister.wsearch.client.SearchQuery;
import org.github.pister.wsearch.client.SearchResult;
import org.github.pister.wsearch.client.doc.Document;
import org.github.pister.wsearch.client.doc.Schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午10:18
 */
public class SearchHandler {

    private Schema schema;

    private IndexSearcher indexSearcher;

    private IndexWriter indexWriter;

    private IndexReader indexReader;

    private Directory directory;

    public void init() throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, null);
        indexWriter = new IndexWriter(directory, indexWriterConfig);
        indexReader = IndexReader.open(indexWriter, true);
        indexSearcher = new IndexSearcher(indexReader);
    }

    private Document idToDocument(ScoreDoc scoreDoc) {
        Document document = new Document();

        return document;
    }

    public SearchResult search(SearchQuery searchQuery) {
        Query query = null;
        Filter filter = null;
        int pageSize = 20;
        Sort sort = null;
        try {
            SearchResult searchResult = new SearchResult();
            TopFieldDocs topFieldDocs = indexSearcher.search(query, filter, pageSize, sort);
            searchResult.setTotalHits(topFieldDocs.totalHits);
            ScoreDoc[] scoreDocs = topFieldDocs.scoreDocs;
            List<Document> documents = new ArrayList<Document>(scoreDocs.length);
            for (ScoreDoc scoreDoc : scoreDocs) {
                documents.add(idToDocument(scoreDoc));
            }
            searchResult.setDocuments(documents);
            return searchResult;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void saveDocument(Document document) {

    }

    public void deleteDocument(String id) {
        try {
            indexWriter.deleteDocuments(new Term(schema.getIdName(), id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

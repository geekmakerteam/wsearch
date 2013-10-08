package org.github.pister.wsearch.core.searcher.engine;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.schema.FileDataDirectory;
import org.github.pister.wsearch.core.schema.RAMDataDirectory;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.schema.DataDirectory;
import org.github.pister.wsearch.core.searcher.query.FieldSort;
import org.github.pister.wsearch.core.searcher.query.OutputDocument;
import org.github.pister.wsearch.core.searcher.query.SearchQuery;
import org.github.pister.wsearch.core.searcher.response.QueryResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午10:18
 */
public class DefaultSearchEngineTest extends TestCase {

    public void testInit() {
        DefaultSearchEngine embedSearchServer = initSearchEngine("hello");
        embedSearchServer.close();
    }

    private DefaultSearchEngine initSearchEngine(String name) {
        String schemaPath = "/Users/longyi/temp/" + name;
        DataDirectory dataDirectory = new FileDataDirectory(schemaPath);
        return initSearchEngine(dataDirectory);
    }

    private DefaultSearchEngine initSearchEngine() {
        return initSearchEngine(new RAMDataDirectory());
    }

    private DefaultSearchEngine initSearchEngine(DataDirectory dataDirectory) {
        Schema schema = new Schema();
        {
            FieldInfo fieldInfo = new FieldInfo("id");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(false);
            schema.addFieldInfo(fieldInfo);
        }
        {
            FieldInfo fieldInfo = new FieldInfo("age");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(true);
            fieldInfo.setType("int");
            schema.addFieldInfo(fieldInfo);
        }
        {
            FieldInfo fieldInfo = new FieldInfo("name");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(true);
            schema.addFieldInfo(fieldInfo);
        }
        {
            FieldInfo fieldInfo = new FieldInfo("addr");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(true);
            schema.addFieldInfo(fieldInfo);
        }
        {
            FieldInfo fieldInfo = new FieldInfo("desc");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(false);
            fieldInfo.setType("text");
            schema.addFieldInfo(fieldInfo);
        }
        {
            FieldInfo fieldInfo = new FieldInfo("update_date");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(true);
            fieldInfo.setType("date");
            schema.addFieldInfo(fieldInfo);
        }
        DefaultSearchEngine searchEngine = new DefaultSearchEngine(dataDirectory, schema);
        return searchEngine;

    }

    private void addDocument(DefaultSearchEngine embedSearchServer) {
        {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", "1");
            inputDocument.addField("name", "pister");
            inputDocument.addField("age", "32");
            inputDocument.addField("addr", "hangzhou");
            inputDocument.addField("desc", "i am has the biggest header in my family");
            inputDocument.addField("update_date", new Date());
            embedSearchServer.add(inputDocument);
        }
        {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", 2);
            inputDocument.addField("name", "jack");
            inputDocument.addField("age", 48);
            inputDocument.addField("addr", "hangzhou");
            inputDocument.addField("desc", "aha, i ma jack");
            inputDocument.addField("update_date", new Date());
            embedSearchServer.add(inputDocument);
        }
    }

    public void testAdd() {
        DefaultSearchEngine searchEngine = initSearchEngine("hello");
        try {
            addDocument(searchEngine);
        } finally {
            searchEngine.close();
        }
    }

    public void testForSort() {
        DefaultSearchEngine searchEngine = initSearchEngine();
        {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", 1);
            inputDocument.addField("name", "jack");
            inputDocument.addField("age", 10);
            inputDocument.addField("update_date", new Date());
            searchEngine.add(inputDocument);
        }
        {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", 2);
            inputDocument.addField("name", "pister");
            inputDocument.addField("age", 20);
            inputDocument.addField("update_date", new Date());
            searchEngine.add(inputDocument);
        }
        {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", 3);
            inputDocument.addField("name", "ally");
            inputDocument.addField("age", 5);
            inputDocument.addField("update_date", new Date());
            searchEngine.add(inputDocument);
        }
        searchEngine.commit();
        {
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setQuery("*:*");
            searchQuery.setFieldSorts(Arrays.asList(new FieldSort("age", FieldSort.ASC)));
            QueryResponse queryResponse = searchEngine.query(searchQuery);
            Assert.assertEquals(3, queryResponse.getTotalHits());
            List<OutputDocument> outputDocuments = queryResponse.getOutputDocuments();
            Assert.assertEquals(3, outputDocuments.size());
            Assert.assertEquals("ally", outputDocuments.get(0).getField("name"));
            Assert.assertEquals("jack", outputDocuments.get(1).getField("name"));
            Assert.assertEquals("pister", outputDocuments.get(2).getField("name"));
        }
    }

    public void testForPage() {
        DefaultSearchEngine searchEngine = initSearchEngine();
        final int total = 500;
        for (int i = 1; i <= 500; i++) {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", i);
            inputDocument.addField("name", "name" + i);
            inputDocument.addField("age", i / 10);
            inputDocument.addField("addr", "hangzhou" + i);
            inputDocument.addField("desc", "aha, i ma jack " + i);
            inputDocument.addField("update_date", new Date());
            searchEngine.add(inputDocument);
        }
        searchEngine.commit();
        int pageSize = 20;
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setPageNo(1);
        searchQuery.setPageSize(pageSize);
        searchQuery.setQuery("*:*");
        QueryResponse queryResponse = searchEngine.query(searchQuery);
        int hits = queryResponse.getTotalHits();
        Assert.assertEquals(total, hits);
        assertResults(queryResponse.getOutputDocuments(), 1, pageSize);
        int pageCount = (total + pageSize - 1) / pageSize;
        for (int pageNo = 1; pageNo <= pageCount; ++pageNo) {
            SearchQuery query = new SearchQuery();
            query.setPageNo(pageNo);
            query.setPageSize(pageSize);
            query.setQuery("*:*");
            QueryResponse response = searchEngine.query(query);
            Assert.assertEquals(total, response.getTotalHits());
            assertResults(response.getOutputDocuments(), pageNo, pageSize);
            System.out.println("pageNo " + pageNo + " test finish!");
        }

        searchEngine.close();
    }

    private void assertResults(List<OutputDocument> outputDocuments, int pageNo, int pageSize) {
        int i = 1;
        for (OutputDocument outputDocument : outputDocuments) {
            String name = (String) outputDocument.getFields().get("name");
            Assert.assertEquals("pageNo:" + pageNo, "name" + (pageSize * (pageNo - 1) + i), name);
            i++;
        }
    }

    public void testQuery() {
        DefaultSearchEngine searchEngine = initSearchEngine("hello");
        try {
            searchEngine.deleteById("1");
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setQuery("*:*");
            QueryResponse queryResponse = searchEngine.query(searchQuery);
            System.out.println(queryResponse.isSuccess());
            System.out.println(queryResponse.getTotalHits());
            for (OutputDocument outputDocument : queryResponse.getOutputDocuments()) {
                System.out.println(outputDocument.getFields());
            }
        } finally {
            searchEngine.close();
        }
    }
}

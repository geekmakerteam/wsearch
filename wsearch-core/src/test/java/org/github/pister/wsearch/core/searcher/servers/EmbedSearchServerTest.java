package org.github.pister.wsearch.core.searcher.servers;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.schema.FileSchemaMeta;
import org.github.pister.wsearch.core.schema.RAMSchemaMeta;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.schema.SchemaMeta;
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
public class EmbedSearchServerTest extends TestCase {

    public void testInit() {
        EmbedSearchServer embedSearchServer = initEmbedSearchServer("hello");
        embedSearchServer.close();
    }

    private EmbedSearchServer initEmbedSearchServer(String name) {
        String schemaPath = "/Users/longyi/temp/" + name;
        SchemaMeta schemaMeta = new FileSchemaMeta(schemaPath);
        return initEmbedSearchServer(schemaMeta);
    }

    private EmbedSearchServer initEmbedSearchServer() {
        return initEmbedSearchServer(new RAMSchemaMeta());
    }

    private EmbedSearchServer initEmbedSearchServer(SchemaMeta schemaMeta) {
        Schema schema = new Schema(schemaMeta);
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
        EmbedSearchServer embedSearchServer = new EmbedSearchServer(schema);
        embedSearchServer.init();
        return embedSearchServer;

    }

    private void addDocument(EmbedSearchServer embedSearchServer) {
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
        EmbedSearchServer embedSearchServer = initEmbedSearchServer("hello");
        try {
            addDocument(embedSearchServer);
        } finally {
            embedSearchServer.close();
        }
    }

    public void testForSort() {
        EmbedSearchServer embedSearchServer = initEmbedSearchServer();
        {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", 1);
            inputDocument.addField("name", "jack");
            inputDocument.addField("age", 10);
            inputDocument.addField("update_date", new Date());
            embedSearchServer.add(inputDocument);
        }
        {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", 2);
            inputDocument.addField("name", "pister");
            inputDocument.addField("age", 20);
            inputDocument.addField("update_date", new Date());
            embedSearchServer.add(inputDocument);
        }
        {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", 3);
            inputDocument.addField("name", "ally");
            inputDocument.addField("age", 5);
            inputDocument.addField("update_date", new Date());
            embedSearchServer.add(inputDocument);
        }
        embedSearchServer.commit();
        {
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setQuery("*:*");
            searchQuery.setFieldSorts(Arrays.asList(new FieldSort("age", FieldSort.ASC)));
            QueryResponse queryResponse = embedSearchServer.query(searchQuery);
            Assert.assertEquals(3, queryResponse.getTotalHits());
            List<OutputDocument> outputDocuments = queryResponse.getOutputDocuments();
            Assert.assertEquals(3, outputDocuments.size());
            Assert.assertEquals("ally", outputDocuments.get(0).getField("name"));
            Assert.assertEquals("jack", outputDocuments.get(1).getField("name"));
            Assert.assertEquals("pister", outputDocuments.get(2).getField("name"));
        }
    }

    public void testForPage() {
        EmbedSearchServer embedSearchServer = initEmbedSearchServer();
        final int total = 500;
        for (int i = 1; i <= 500; i++) {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", i);
            inputDocument.addField("name", "name" + i);
            inputDocument.addField("age", i / 10);
            inputDocument.addField("addr", "hangzhou" + i);
            inputDocument.addField("desc", "aha, i ma jack " + i);
            inputDocument.addField("update_date", new Date());
            embedSearchServer.add(inputDocument);
        }
        embedSearchServer.commit();
        int pageSize = 20;
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setPageNo(1);
        searchQuery.setPageSize(pageSize);
        searchQuery.setQuery("*:*");
        QueryResponse queryResponse = embedSearchServer.query(searchQuery);
        int hits = queryResponse.getTotalHits();
        Assert.assertEquals(total, hits);
        assertResults(queryResponse.getOutputDocuments(), 1, pageSize);
        int pageCount = (total + pageSize - 1) / pageSize;
        for (int pageNo = 1; pageNo <= pageCount; ++pageNo) {
            SearchQuery query = new SearchQuery();
            query.setPageNo(pageNo);
            query.setPageSize(pageSize);
            query.setQuery("*:*");
            QueryResponse response = embedSearchServer.query(query);
            Assert.assertEquals(total, response.getTotalHits());
            assertResults(response.getOutputDocuments(), pageNo, pageSize);
            System.out.println("pageNo " + pageNo + " test finsh!");
        }

        embedSearchServer.close();
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
        EmbedSearchServer embedSearchServer = initEmbedSearchServer("hello");
        try {
            embedSearchServer.deleteById("1");
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setQuery("*:*");
            QueryResponse queryResponse = embedSearchServer.query(searchQuery);
            System.out.println(queryResponse.isSuccess());
            System.out.println(queryResponse.getTotalHits());
            for (OutputDocument outputDocument : queryResponse.getOutputDocuments()) {
                System.out.println(outputDocument.getFields());
            }
        } finally {
            embedSearchServer.close();
        }
    }
}

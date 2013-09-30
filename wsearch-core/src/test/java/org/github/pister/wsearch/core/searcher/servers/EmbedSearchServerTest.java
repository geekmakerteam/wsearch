package org.github.pister.wsearch.core.searcher.servers;

import junit.framework.TestCase;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.schema.SchemaMeta;
import org.github.pister.wsearch.core.searcher.query.OutputDocument;
import org.github.pister.wsearch.core.searcher.query.SearchQuery;
import org.github.pister.wsearch.core.searcher.response.QueryResponse;
import org.github.pister.wsearch.core.util.date.DateFormatter;

import java.util.Date;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午10:18
 */
public class EmbedSearchServerTest extends TestCase {

    public void testInit() {
        EmbedSearchServer embedSearchServer = initEmbedSearchServer();
        embedSearchServer.close();
    }

    private EmbedSearchServer initEmbedSearchServer() {
        String schemaPath = "/Users/longyi/temp/hello";
        SchemaMeta schemaMeta = new SchemaMeta(schemaPath);
        Schema schema = new Schema(schemaMeta);
        {
            FieldInfo fieldInfo = new FieldInfo("id");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(false);
            schema.addFieldInfo(fieldInfo);
        }
        {
            FieldInfo fieldInfo = new FieldInfo("age");
            fieldInfo.setIndex(false);
            fieldInfo.setStore(true);
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
            inputDocument.addField("update_date", DateFormatter.formatDateYMDHMS(new Date()));
            embedSearchServer.add(inputDocument);
        }
        {
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", "2");
            inputDocument.addField("name", "jack");
            inputDocument.addField("age", "48");
            inputDocument.addField("addr", "hangzhou");
            inputDocument.addField("desc", "aha, i ma jack");
            inputDocument.addField("update_date", DateFormatter.formatDateYMDHMS(new Date()));
            embedSearchServer.add(inputDocument);
        }
    }

    public void testAdd() {
        EmbedSearchServer embedSearchServer = initEmbedSearchServer();
        try {
            addDocument(embedSearchServer);
        } finally {
            embedSearchServer.close();
        }
    }

    public void testQuery() {
        EmbedSearchServer embedSearchServer = initEmbedSearchServer();
        try {
            addDocument(embedSearchServer);
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setQuery("desc:jack");
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

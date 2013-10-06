package org.github.pister.wsearch.core.searcher.servers;

import junit.framework.TestCase;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.schema.FileSchemaMeta;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.schema.SchemaMeta;
import org.github.pister.wsearch.core.searcher.query.SearchQuery;
import org.github.pister.wsearch.core.searcher.response.QueryResponse;

import java.util.Date;
import java.util.Random;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午11:35
 */
public class ConcurrentReadWriterTest extends TestCase {

    Thread writerThread = new Thread() {

        private Random random = new Random(System.currentTimeMillis());
        private int seq = 1;

        private int getNextId() {
            return seq++;
        }

        private int randExistId() {
            return random.nextInt(seq);
        }

        @Override
        public void run() {
            {
                int id = getNextId();
                InputDocument inputDocument = new InputDocument();
                inputDocument.addField("id", id);
                inputDocument.addField("update_date", new Date());
                embedSearchServer.add(inputDocument);
            }
            int loopCount = 0;
            while (true) {
                int ran = random.nextInt(10);
                InputDocument inputDocument = new InputDocument();
                inputDocument.addField("name", "name-" + loopCount);
                if (ran < 6) {
                    // for update
                    int id = randExistId();
                    inputDocument.addField("id", id);
                    inputDocument.addField("update_date", new Date());
                    embedSearchServer.add(inputDocument);
                } else if (ran < 9) {
                    // 6,7,8 for new
                    int id = getNextId();
                    inputDocument.addField("id", id);
                    inputDocument.addField("update_date", new Date());
                    embedSearchServer.add(inputDocument);
                } else {
                    // 9 for delete
                    int id = randExistId();
                    embedSearchServer.deleteById(String.valueOf(id));
                }
                loopCount++;
                try {
                    Thread.sleep(random.nextInt(2000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private DefaultSearchEngine embedSearchServer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String schemaPath = "/Users/longyi/temp/concurrent_test";
        SchemaMeta schemaMeta = new FileSchemaMeta(schemaPath);
        Schema schema = new Schema(schemaMeta);
        {
            FieldInfo fieldInfo = new FieldInfo("id");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(false);
            schema.addFieldInfo(fieldInfo);
        }
        {
            FieldInfo fieldInfo = new FieldInfo("name");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(true);
            schema.addFieldInfo(fieldInfo);
        }
        {
            FieldInfo fieldInfo = new FieldInfo("update_date");
            fieldInfo.setIndex(true);
            fieldInfo.setStore(true);
            fieldInfo.setType("date");
            schema.addFieldInfo(fieldInfo);
        }
        DefaultSearchEngine embedSearchServer = new DefaultSearchEngine(schema);
        embedSearchServer.init();

        this.embedSearchServer = embedSearchServer;
    }

    public void testConcurrent() throws InterruptedException {
        // start writer thread
        try {
            writerThread.start();
            // start read thread
            for (int i = 0; i < 10; ++i) {
                new ReaderThread().start();
            }
            // wait for finish
            Thread.sleep(1 * 60 * 1000);
        } finally {
            embedSearchServer.close();
        }
    }

    class ReaderThread extends Thread {
        @Override
        public void run() {
            while (true) {
                SearchQuery searchQuery = new SearchQuery();
                searchQuery.setQuery("*:*");
                QueryResponse queryResponse = embedSearchServer.query(searchQuery);
                StringBuilder sb = new StringBuilder();
                sb.append("total:");
                sb.append(queryResponse.getTotalHits());
                sb.append("\n");
                sb.append(queryResponse.getOutputDocuments());
                System.out.println(sb.toString());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package org.github.pister.wsearch.core.searcher.servers;

import junit.framework.TestCase;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.schema.SchemaMeta;

import java.util.Random;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午11:35
 */
public class ConcurrentReadWriterTest extends TestCase {

    private EmbedSearchServer embedSearchServer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String schemaPath = "/Users/longyi/temp/concurrent_test";
        SchemaMeta schemaMeta = new SchemaMeta(schemaPath);
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
    }

    public void testConcurrent() throws InterruptedException {
        // start writer thread
        writerThread.start();
        // start read thread
        for (int i = 0; i < 1; ++i) {
            new ReaderThread().start();
        }
        // wait for finish
        Thread.sleep(10 * 60 * 1000);
        embedSearchServer.close();
    }


    Thread writerThread = new Thread() {

        private Random random = new Random(System.currentTimeMillis());

        private int seq = 1;


        @Override
        public void run() {
            while (true) {
                int ran = random.nextInt(10);
                InputDocument inputDocument = new InputDocument();
                inputDocument.addField("name", "");
                if (ran < 6) {
                    // for new
                } else if (ran < 9) {
                    // 6,7,8 for new
                } else {
                    // 9 for delete
                }

                embedSearchServer.add(inputDocument);
            }
        }
    };

    class ReaderThread extends Thread {
        @Override
        public void run() {
        }
    }
}

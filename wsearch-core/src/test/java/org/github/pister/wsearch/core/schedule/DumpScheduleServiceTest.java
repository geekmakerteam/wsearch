package org.github.pister.wsearch.core.schedule;

import junit.framework.TestCase;
import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.schedule.dump.DelayTimeFullScheduler;
import org.github.pister.wsearch.core.schedule.dump.DelayTimeIncrScheduler;
import org.github.pister.wsearch.core.schedule.timerange.RamTimeRangeService;
import org.github.pister.wsearch.core.schema.FileDataDirectory;
import org.github.pister.wsearch.core.schema.RAMDataDirectory;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.searcher.SearchEngine;
import org.github.pister.wsearch.core.searcher.engine.DefaultSearchEngine;
import org.github.pister.wsearch.core.searcher.engine.SearchEngineWrapper;
import org.github.pister.wsearch.core.searcher.query.SearchQuery;
import org.github.pister.wsearch.core.searcher.response.QueryResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * User: longyi
 * Date: 13-10-8
 * Time: 下午3:29
 */
public class DumpScheduleServiceTest extends TestCase {

    SearchEngine searchEngine;


    private Schema getSchema() {
        Schema schema = new Schema();
        schema.addFieldInfo(new FieldInfo("id").setIndex(true).setStore(true));
        schema.addFieldInfo(new FieldInfo("name").setIndex(true).setStore(true).setType("text"));
        schema.addFieldInfo(new FieldInfo("updateDate").setIndex(false).setStore(true));
        return schema;
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        searchEngine = new DefaultSearchEngine(new FileDataDirectory("/Users/longyi/temp/d3"), getSchema());

    }
    public void testXX() throws InterruptedException {
        SearchEngineWrapper searchEngineWrapper = new SearchEngineWrapper(searchEngine);

        DumpScheduleService dumpScheduleService = new DumpScheduleService(searchEngineWrapper);
       // dumpScheduleService.registerIncrDump(DelayTimeIncrScheduler.class, new Object[] {10, new RamTimeRangeService()}, new DataProviderIncr());
        dumpScheduleService.registerFullDump(DelayTimeFullScheduler.class, new Object[] {300}, new DataProviderFull(), searchEngineWrapper);

        dumpScheduleService.start();


        for (int i = 0; i < 1000; ++i) {
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setQuery("*:*");
            QueryResponse queryResponse = searchEngineWrapper.query(searchQuery);
            System.out.println(queryResponse.getTotalHits());
            System.out.println(queryResponse.getOutputDocuments());
            Thread.sleep(1000);
        }

    }

    static class DataProviderFull extends DataProviderIncr {

        private Random random = new Random(System.currentTimeMillis());

        public void init() {
            books = Arrays.asList(
                    new Book(21, "222thinking in c++"),
                    new Book(22, "222流量的密码"),
                    new Book(23, "222精通css"),
                    new Book(24, "222effective c++"),
                    new Book(25, "222世界是平的"),
                    new Book(26, "222ajax in action"),
                    new Book(27, "222算法导论"),
                    new Book(28, "222python核心编程"),
                    new Book(29, "222编译器工程"),
                    new Book(30, "222编译原理"),
                    new Book(31, "222python cookbook"),
                    new Book(32, "222javascript高级程序设计")
            );
            booksIterator = books.iterator();
            System.out.println("dump full!!!!");
        }

        @Override
        public InputDocument next() {
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return super.next();
        }
    }

    static class DataProviderIncr implements DataProvider {
        protected List<Book> books;
        protected Iterator<Book> booksIterator;

        @Override
        public void init() {
            books = Arrays.asList(
                    new Book(1, "thinking in c++"),
                    new Book(2, "流量的密码"),
                    new Book(3, "精通css"),
                    new Book(4, "effective c++"),
                    new Book(5, "世界是平的"),
                    new Book(6, "ajax in action"),
                    new Book(7, "算法导论"),
                    new Book(8, "python核心编程"),
                    new Book(9, "编译器工程"),
                    new Book(10, "编译原理"),
                    new Book(11, "python cookbook"),
                    new Book(12, "javascript高级程序设计")
            );
            booksIterator = books.iterator();
            System.out.println("dump incr!!!!");
        }

        @Override
        public void close() {

        }

        @Override
        public boolean hasNext() {
            return booksIterator.hasNext();
        }

        @Override
        public InputDocument next() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Book book = booksIterator.next();
            InputDocument inputDocument = new InputDocument();
            inputDocument.addField("id", book.id);
            inputDocument.addField("name", book.name);
            inputDocument.addField("updateDate", book.updateDate);
            return inputDocument;
        }
    }

    static class Book {
        int id;
        String name;
        Date updateDate;

        Book(int id, String name) {
            this.id = id;
            this.name = name;
            this.updateDate = new Date();
        }
    }

}

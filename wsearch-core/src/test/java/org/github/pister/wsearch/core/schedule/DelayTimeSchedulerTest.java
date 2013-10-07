package org.github.pister.wsearch.core.schedule;

import junit.framework.TestCase;
import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.schema.RAMSchemaMeta;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.searcher.SearchEngine;
import org.github.pister.wsearch.core.searcher.engine.DefaultSearchEngine;
import org.github.pister.wsearch.core.searcher.query.SearchQuery;
import org.github.pister.wsearch.core.searcher.response.QueryResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午11:33
 */
public class DelayTimeSchedulerTest extends TestCase {
    SearchEngine searchEngine;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Schema schema = new Schema(new RAMSchemaMeta());
        schema.addFieldInfo(new FieldInfo("id").setIndex(true).setStore(true));
        schema.addFieldInfo(new FieldInfo("name").setIndex(true).setStore(true).setType("text"));
        schema.addFieldInfo(new FieldInfo("updateDate").setIndex(false).setStore(true));
        searchEngine = new DefaultSearchEngine(schema);

    }

    public void testDump() throws InterruptedException {
        DelayTimeScheduler delayTimeScheduler = new DelayTimeScheduler(0, 10);

        DataProvider dataProvider = new DataProvider() {

            private List<Book> books;
            private Iterator<Book> booksIterator;

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
                System.out.println("dump!!!!");
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
                Book book = booksIterator.next();
                InputDocument inputDocument = new InputDocument();
                inputDocument.addField("id", book.id);
                inputDocument.addField("name", book.name);
                inputDocument.addField("updateDate", book.updateDate);
                return inputDocument;
            }
        };

        delayTimeScheduler.startSchedule(dataProvider, searchEngine);

        for (int i = 0; i < 100; ++i) {
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setQuery("name:*");
            QueryResponse queryResponse = searchEngine.query(searchQuery);
            System.out.println(queryResponse.getTotalHits());
            System.out.println(queryResponse.getOutputDocuments());
            Thread.sleep(1000);
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

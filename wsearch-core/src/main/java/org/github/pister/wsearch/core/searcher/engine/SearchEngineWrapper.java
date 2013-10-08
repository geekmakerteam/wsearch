package org.github.pister.wsearch.core.searcher.engine;

import org.github.pister.wsearch.core.doc.InputDocument;
import org.github.pister.wsearch.core.schedule.SearchEngineSwitchCallback;
import org.github.pister.wsearch.core.schema.DataDirectory;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.searcher.SearchEngine;
import org.github.pister.wsearch.core.searcher.query.SearchQuery;
import org.github.pister.wsearch.core.searcher.response.AddResponse;
import org.github.pister.wsearch.core.searcher.response.DeleteResponse;
import org.github.pister.wsearch.core.searcher.response.OperationResponse;
import org.github.pister.wsearch.core.searcher.response.QueryResponse;

import java.util.Collection;
import java.util.List;

/**
 * User: longyi
 * Date: 13-10-8
 * Time: 下午12:36
 */
public class SearchEngineWrapper implements SearchEngine, SearchEngineSwitchCallback {

    private volatile SearchEngine currenctSearchEngine;

    private volatile SearchEngine oldSearchEngine;

    public SearchEngineWrapper(SearchEngine searchEngine) {
        this.currenctSearchEngine = searchEngine;
    }

    @Override
    public synchronized void onSwitchSearchEngine(SearchEngine newSearchEngine) {
        this.oldSearchEngine = currenctSearchEngine;
        this.currenctSearchEngine = newSearchEngine;
    }

    public void closeOld() {
       if (oldSearchEngine != null) {
           oldSearchEngine.close();
       }

    }

    public AddResponse add(Collection<InputDocument> inputDocument) {
        return currenctSearchEngine.add(inputDocument);
    }

    public AddResponse add(InputDocument inputDocument) {
        return currenctSearchEngine.add(inputDocument);
    }

    public OperationResponse rollback() {
        return currenctSearchEngine.rollback();
    }

    public OperationResponse commitAndOptimize() {
        return currenctSearchEngine.commitAndOptimize();
    }

    public OperationResponse commit() {
        return currenctSearchEngine.commit();
    }

    public OperationResponse optimize() {
        return currenctSearchEngine.optimize();
    }

    public DeleteResponse deleteById(String id) {
        return currenctSearchEngine.deleteById(id);
    }

    public void close() {
        currenctSearchEngine.close();
    }

    public OperationResponse reopen() {
        return currenctSearchEngine.reopen();
    }

    public QueryResponse query(SearchQuery searchQuery) {
        return currenctSearchEngine.query(searchQuery);
    }

    public DeleteResponse deleteByIds(List<String> ids) {
        return currenctSearchEngine.deleteByIds(ids);
    }

    public DataDirectory getDataDirectory() {
        return currenctSearchEngine.getDataDirectory();
    }

    public Schema getSchema() {
        return currenctSearchEngine.getSchema();
    }
}

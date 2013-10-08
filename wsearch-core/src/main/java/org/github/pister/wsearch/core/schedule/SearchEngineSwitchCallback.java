package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.searcher.SearchEngine;

/**
 * User: longyi
 * Date: 13-10-8
 * Time: 上午8:59
 */
public interface SearchEngineSwitchCallback {

    void onSwitchSearchEngine(SearchEngine newSearchEngine);

}

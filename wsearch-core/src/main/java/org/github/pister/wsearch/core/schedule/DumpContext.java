package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.searcher.SearchEngine;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午6:39
 */
public interface DumpContext {

    void setAttribute(String name, Object object);

    Object getAttribute(String name);

    DataProvider getDataProvider();

    SearchEngine getSearchEngine();

    boolean isCancel();

    void setCancel(boolean cancel);

    DumpScheduleService getDumpScheduleService();

    void setDumpScheduleService(DumpScheduleService dumpScheduleService);
}

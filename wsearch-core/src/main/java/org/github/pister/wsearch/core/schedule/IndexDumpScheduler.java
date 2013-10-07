package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.searcher.SearchEngine;

/**
 * User: longyi
 * Date: 13-10-6
 * Time: 下午10:38
 */
public interface IndexDumpScheduler {

    void startSchedule(DataProvider dataProvider, SearchEngine searchEngine);

    void waitForClose(int waitForSeconds) throws InterruptedException;

    void setDumpScheduleService(DumpScheduleService dumpScheduleService);

}

package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.searcher.SearchEngine;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午12:52
 */
public abstract class IncrIndexDumpScheduler extends AbstractIndexDumpScheduler {

    @Override
    protected void onBeforeDump(DataProvider dataProvider, SearchEngine searchEngine) {
        super.onBeforeDump(dataProvider, searchEngine);
        TimeRange timeRange = getTimeRangeService().getTimeRange();
        if (dataProvider instanceof TimeRangeAwire) {
            ((TimeRangeAwire)dataProvider).setTimeRange(timeRange);
        }
    }

    @Override
    protected void onAfterDump(DataProvider dataProvider, SearchEngine searchEngine) {
        super.onAfterDump(dataProvider, searchEngine);
        getTimeRangeService().nextTimeRange();
    }

    protected abstract TimeRangeService getTimeRangeService();

}

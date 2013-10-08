package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.schedule.dump.IndexDumpScheduler;
import org.github.pister.wsearch.core.searcher.SearchEngine;
import org.github.pister.wsearch.core.util.ClassUtil;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午7:41
 */
public class DumpScheduleService {

    private ScheduleInfo fulldumpScheduleInfo;
    private ScheduleInfo incrdumpScheduleInfo;
    private IndexDumpScheduler fullIndexDumpScheduler;
    private IndexDumpScheduler incrIndexDumpScheduler;
    private SearchEngine searchEngine;

    public DumpScheduleService(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public void registerFullDump(Class<? extends IndexDumpScheduler> scheduleClass, Object[] constructorArguments, DataProvider dataProvider, SearchEngineSwitchCallback engineSwitchCallback) {
        fulldumpScheduleInfo = new ScheduleInfo(scheduleClass, constructorArguments, dataProvider, engineSwitchCallback);
    }

    public void registerIncrDump(Class<? extends IndexDumpScheduler> scheduleClass, Object[] constructorArguments, DataProvider dataProvider) {
        incrdumpScheduleInfo = new ScheduleInfo(scheduleClass, constructorArguments, dataProvider, null);
    }

    public void start() {
        this.startFullSchedule();
        this.startIncrSchedule();
    }

    private void startFullSchedule() {
        if (fulldumpScheduleInfo != null) {
            fullIndexDumpScheduler = startSchedule(fulldumpScheduleInfo, null);
        }
    }

    private void startIncrSchedule() {
        if (incrdumpScheduleInfo != null) {
            incrIndexDumpScheduler = startSchedule(incrdumpScheduleInfo, null);
        }
    }

    public void startIncrSchedule(SearchEngine searchEngine) {
        if (incrdumpScheduleInfo != null) {
            incrIndexDumpScheduler = startSchedule(incrdumpScheduleInfo, searchEngine);
        }
    }

    private IndexDumpScheduler startSchedule(ScheduleInfo scheduleInfo, SearchEngine searchEngine) {
        IndexDumpScheduler ret = ClassUtil.newInstance(scheduleInfo.getScheduleClass(), scheduleInfo.getConstructorArguments());
        ret.setDumpScheduleService(this);
        ret.setEngineSwitchCallback(scheduleInfo.getEngineSwitchCallback());
        if (searchEngine == null) {
            searchEngine = this.searchEngine;
        }
        ret.startSchedule(scheduleInfo.getDataProvider(), searchEngine);
        return ret;
    }

    public IndexDumpScheduler getFullIndexDumpScheduler() {
        return fullIndexDumpScheduler;
    }

    public IndexDumpScheduler getIncrIndexDumpScheduler() {
        return incrIndexDumpScheduler;
    }

    public ScheduleInfo getFulldumpScheduleInfo() {
        return fulldumpScheduleInfo;
    }

    public ScheduleInfo getIncrdumpScheduleInfo() {
        return incrdumpScheduleInfo;
    }


}

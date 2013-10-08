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

    public void registerFullDump(Class<? extends IndexDumpScheduler> scheduleClass, DataProvider dataProvider, SearchEngine searchEngine, SearchEngineSwitchCallback engineSwitchCallback) {
        fulldumpScheduleInfo = new ScheduleInfo(scheduleClass, dataProvider, searchEngine, engineSwitchCallback);
    }

    public void registerIncrDump(Class<? extends IndexDumpScheduler> scheduleClass, DataProvider dataProvider, SearchEngine searchEngine, SearchEngineSwitchCallback engineSwitchCallback) {
        incrdumpScheduleInfo = new ScheduleInfo(scheduleClass, dataProvider, searchEngine, engineSwitchCallback);
    }

    public void start() {
        this.startFullSchedule();
        this.startIncrSchedule();
    }

    public void startFullSchedule() {
        if (fulldumpScheduleInfo != null) {
            fullIndexDumpScheduler = startSchedule(fulldumpScheduleInfo);
        }
    }

    public void startIncrSchedule() {
        if (incrdumpScheduleInfo != null) {
            incrIndexDumpScheduler = startSchedule(incrdumpScheduleInfo);
        }
    }

    private IndexDumpScheduler startSchedule(ScheduleInfo scheduleInfo) {
        IndexDumpScheduler ret = ClassUtil.newInstance(scheduleInfo.scheduleClass);
        ret.setDumpScheduleService(this);
        ret.setEngineSwitchCallback(scheduleInfo.engineSwitchCallback);
        ret.startSchedule(scheduleInfo.dataProvider, scheduleInfo.searchEngine);
        return ret;
    }

    public IndexDumpScheduler getFullIndexDumpScheduler() {
        return fullIndexDumpScheduler;
    }

    public IndexDumpScheduler getIncrIndexDumpScheduler() {
        return incrIndexDumpScheduler;
    }

    static class ScheduleInfo {
        Class<? extends IndexDumpScheduler> scheduleClass;
        DataProvider dataProvider;
        SearchEngine searchEngine;
        SearchEngineSwitchCallback engineSwitchCallback;

        ScheduleInfo(Class<? extends IndexDumpScheduler> scheduleClass, DataProvider dataProvider, SearchEngine searchEngine, SearchEngineSwitchCallback engineSwitchCallback) {
            this.scheduleClass = scheduleClass;
            this.dataProvider = dataProvider;
            this.searchEngine = searchEngine;
            this.engineSwitchCallback = engineSwitchCallback;
        }
    }


}

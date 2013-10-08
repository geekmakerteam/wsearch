package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.schedule.dump.IndexDumpScheduler;

/**
 * User: longyi
 * Date: 13-10-8
 * Time: 下午3:41
 */
public class ScheduleInfo {
    private Class<? extends IndexDumpScheduler> scheduleClass;
    private Object[] constructorArguments;
    private DataProvider dataProvider;
    private SearchEngineSwitchCallback engineSwitchCallback;

    public ScheduleInfo(Class<? extends IndexDumpScheduler> scheduleClass, Object[] constructorArguments, DataProvider dataProvider, SearchEngineSwitchCallback engineSwitchCallback) {
        this.scheduleClass = scheduleClass;
        this.constructorArguments = constructorArguments;
        this.dataProvider = dataProvider;
        this.engineSwitchCallback = engineSwitchCallback;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public SearchEngineSwitchCallback getEngineSwitchCallback() {
        return engineSwitchCallback;
    }

    public void setEngineSwitchCallback(SearchEngineSwitchCallback engineSwitchCallback) {
        this.engineSwitchCallback = engineSwitchCallback;
    }

    public Class<? extends IndexDumpScheduler> getScheduleClass() {
        return scheduleClass;
    }

    public void setScheduleClass(Class<? extends IndexDumpScheduler> scheduleClass) {
        this.scheduleClass = scheduleClass;
    }

    public Object[] getConstructorArguments() {
        return constructorArguments;
    }

    public void setConstructorArguments(Object[] constructorArguments) {
        this.constructorArguments = constructorArguments;
    }
}

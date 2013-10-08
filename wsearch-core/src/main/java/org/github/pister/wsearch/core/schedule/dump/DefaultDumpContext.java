package org.github.pister.wsearch.core.schedule.dump;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.schedule.DumpScheduleService;
import org.github.pister.wsearch.core.searcher.SearchEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午6:40
 */
public class DefaultDumpContext implements DumpContext {

    private Map<String, Object> attributes = new HashMap<String, Object>();

    private DumpScheduleService dumpScheduleService;
    private boolean cancel;

    public DefaultDumpContext() {
    }

    @Override
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public DumpScheduleService getDumpScheduleService() {
        return dumpScheduleService;
    }

    @Override
    public void setDumpScheduleService(DumpScheduleService dumpScheduleService) {
        this.dumpScheduleService = dumpScheduleService;
    }

}

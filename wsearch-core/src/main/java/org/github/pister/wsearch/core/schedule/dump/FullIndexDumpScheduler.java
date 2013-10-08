package org.github.pister.wsearch.core.schedule.dump;

import org.github.pister.wsearch.core.schema.DataDirectory;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.searcher.SearchEngine;
import org.github.pister.wsearch.core.searcher.engine.DefaultSearchEngine;
import org.github.pister.wsearch.core.searcher.engine.SearchEngineWrapper;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午7:56
 */
public abstract class FullIndexDumpScheduler extends AbstractIndexDumpScheduler {

    protected SearchEngine oldSearchEngine;

    protected void closeOldSearchEngine() {
        if (oldSearchEngine instanceof SearchEngineWrapper) {
            ((SearchEngineWrapper)oldSearchEngine).closeOld();
        }  else {
            oldSearchEngine.close();
        }
    }

    @Override
    protected void onAfterDump(DumpContext dumpContext) {
        super.onAfterDump(dumpContext);
        if (!dumpContext.isCancel()) {
            //索引切换
            try {
                if (engineSwitchCallback != null) {
                    log.warn("switching index...");
                    engineSwitchCallback.onSwitchSearchEngine(currentSearchEngine);
                    currentSearchEngine.getDataDirectory().saveConf();
                    log.warn("switch index finish.");
                }
                closeOldSearchEngine();
            } catch (Exception e) {
                log.error("switch search engine error", e);
            }
        } else {
            SearchEngine tempSearchEngine = currentSearchEngine;
            currentSearchEngine = oldSearchEngine;
            tempSearchEngine.close();
        }
        // 打开增量
        if (dumpScheduleService != null) {
            log.warn("starting increment build job.");
            dumpScheduleService.startIncrSchedule();
        }
    }

    @Override
    protected void onBeforeDump(DumpContext dumpContext) {
        super.onBeforeDump(dumpContext);
        // 关闭增量
        try {
            if (dumpScheduleService != null) {
                log.warn("start canceling increment build job...");
                IndexDumpScheduler incrIndexDumpScheduler = dumpScheduleService.getIncrIndexDumpScheduler();
                if (incrIndexDumpScheduler != null) {
                    incrIndexDumpScheduler.waitForClose(10 * 60);
                }
                log.warn("cancel increment build job finish.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 创建新索引
        oldSearchEngine = currentSearchEngine;
        if (log.isDebugEnabled()) {
            log.debug("oldSearchEngine DataDirectory is:" + oldSearchEngine.getDataDirectory());
        }
        log.warn("creating new search engine...");
        currentSearchEngine = createNextSearchEngine(currentSearchEngine);
        log.warn("create new search engine finish.");
        if (log.isDebugEnabled()) {
            log.debug("newSearchEngine DataDirectory is:" + currentSearchEngine.getDataDirectory());
        }
    }

    private SearchEngine createNextSearchEngine(SearchEngine searchEngine) {
        Schema schema = searchEngine.getSchema();
        DataDirectory newDataDirectory = searchEngine.getDataDirectory().createNextDataDirectory();
        if (log.isDebugEnabled()) {
            log.debug("new dataDirectory:" + newDataDirectory);
            log.debug("clearing data directory...");
        }
        newDataDirectory.clear();
        if (log.isDebugEnabled()) {
            log.debug("clear data directory finish.");
        }
        return new DefaultSearchEngine(newDataDirectory, schema);
    }

}

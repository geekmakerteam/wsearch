package org.github.pister.wsearch.core.schedule;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午7:56
 */
public class FullIndexDumpScheduler extends AbstractIndexDumpScheduler {
    @Override
    protected void onAfterDump(DumpContext dumpContext) {
        super.onAfterDump(dumpContext);
        // 打开增量
        log.warn("starting increment build job.");
        dumpScheduleService.startIncrSchedule();
    }

    @Override
    protected void onBeforeDump(DumpContext dumpContext) {
        super.onBeforeDump(dumpContext);
        // 关闭增量
        try {
            log.warn("start canceling increment build job...");
            dumpScheduleService.getIncrIndexDumpScheduler().waitForClose(10 * 60);
            log.warn("cancel increment build job finish.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void onCancel() {
        // TODO

    }

    @Override
    protected Trigger getTrigger() {
        // TODO
        return null;
    }
}

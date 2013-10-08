package org.github.pister.wsearch.core.schedule.handler;

import org.github.pister.wsearch.core.schedule.dump.Trigger;

/**
 * User: longyi
 * Date: 13-10-8
 * Time: 上午9:55
 */
public interface ScheduleHandler {

    void onCancel();

    Trigger getTrigger();

}

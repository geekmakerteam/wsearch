package org.github.pister.wsearch.core.schedule;

import java.util.Date;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午10:56
 */
public class TimeRange {

    private Date start;

    private Date end;

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }
}

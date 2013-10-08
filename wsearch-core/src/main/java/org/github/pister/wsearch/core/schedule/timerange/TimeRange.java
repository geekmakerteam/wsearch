package org.github.pister.wsearch.core.schedule.timerange;

import java.io.Serializable;
import java.util.Date;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午10:56
 */
public class TimeRange implements Serializable {

    private static final long serialVersionUID = 2624354235359018559L;

    private Date start;

    private Date end;

    public TimeRange(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public TimeRange() {
    }

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

    @Override
    public String toString() {
        return "TimeRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}

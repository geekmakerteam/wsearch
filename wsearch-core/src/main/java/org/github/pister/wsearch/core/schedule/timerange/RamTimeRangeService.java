package org.github.pister.wsearch.core.schedule.timerange;

import org.github.pister.wsearch.core.schedule.timerange.StringStoreTimeRangeService;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午11:51
 */
public class RamTimeRangeService extends StringStoreTimeRangeService {

    private String holder;

    @Override
    protected String load() {
        return holder;
    }

    @Override
    protected void store(String content) {
        this.holder = content;
    }
}

package org.github.pister.wsearch.core.searcher.query;

import java.io.Serializable;
import java.util.List;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 上午11:06
 */
public class SearchQuery implements Serializable {

    private static final long serialVersionUID = -3631454362128879058L;

    private String query;

    private int pageNo = 1;

    private int pageSize = 20;

    private List<FieldSort> fieldSorts;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        if (pageNo < 1) {
            pageNo = 1;
        }
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 1) {
            pageSize = 1;
        }
        this.pageSize = pageSize;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<FieldSort> getFieldSorts() {
        return fieldSorts;
    }

    public void setFieldSorts(List<FieldSort> fieldSorts) {
        this.fieldSorts = fieldSorts;
    }

}

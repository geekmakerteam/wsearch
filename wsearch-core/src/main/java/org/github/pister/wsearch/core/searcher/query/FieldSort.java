package org.github.pister.wsearch.core.searcher.query;

import java.io.Serializable;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午8:54
 */
public class FieldSort implements Serializable {

    private static final long serialVersionUID = 3595748119795085456L;

    public static final int ASC = 0;
    public static final int DESC = 1;
    private String name;
    private int order;

    public FieldSort() {
    }

    public FieldSort(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

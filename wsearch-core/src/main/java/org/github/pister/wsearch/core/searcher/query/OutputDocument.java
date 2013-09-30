package org.github.pister.wsearch.core.searcher.query;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午5:46
 */
public class OutputDocument implements Serializable {
    private static final long serialVersionUID = 6936934393733304930L;

    private Map<String, Object> fields = new HashMap<String, Object>();

    public Map<String, Object> getFields() {
        return fields;
    }

    public void addField(String name, Object value) {
        fields.put(name, value);
    }

    public Object getField(String name) {
        return fields.get(name);
    }

    public String toString() {
        return "OutputDocument:" + fields;
    }
}

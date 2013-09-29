package org.github.pister.wsearch.core.doc;

import java.io.Serializable;
import java.util.Map;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午9:50
 */
public class InputDocument implements Serializable {

    private static final long serialVersionUID = -7666686778357266085L;
    private Map<String, String> fields;

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}

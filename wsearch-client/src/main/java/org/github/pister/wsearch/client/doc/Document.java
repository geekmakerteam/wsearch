package org.github.pister.wsearch.client.doc;

import java.io.Serializable;
import java.util.Map;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午9:50
 */
public class Document implements Serializable {

    private static final long serialVersionUID = -7666686778357266085L;
    private Map<String, Field> fields;

}

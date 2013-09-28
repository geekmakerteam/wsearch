package org.github.pister.wsearch.client.doc;

import java.io.Serializable;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午10:22
 */
public class Schema implements Serializable {

    private static final long serialVersionUID = -903262651256505953L;

    private String idName;

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getIdName() {
        return idName;
    }
}

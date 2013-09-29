package org.github.pister.wsearch.core.searcher.response;

import java.io.Serializable;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 上午11:03
 */
public class Response implements Serializable {

    private static final long serialVersionUID = -3515708540754274652L;

    private int resultCode = ResultCodes.SUCCESS;

    private String message;

    public Response() {
    }

    public Response(String message, int resultCode) {
        this.message = message;
        this.resultCode = resultCode;
    }

    public boolean isSuccess() {
        return resultCode == ResultCodes.SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}

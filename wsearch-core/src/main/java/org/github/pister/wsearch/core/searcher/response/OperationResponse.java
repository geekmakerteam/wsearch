package org.github.pister.wsearch.core.searcher.response;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午5:19
 */
public class OperationResponse extends Response {
    private static final long serialVersionUID = -8300297993247778980L;

    public OperationResponse() {
    }

    public OperationResponse(String message, int resultCode) {
        super(message, resultCode);
    }
}

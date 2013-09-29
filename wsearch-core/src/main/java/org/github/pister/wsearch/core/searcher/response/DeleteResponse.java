package org.github.pister.wsearch.core.searcher.response;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午5:17
 */
public class DeleteResponse extends Response {
    private static final long serialVersionUID = 545976127751930762L;

    public DeleteResponse() {
    }

    public DeleteResponse(String message, int resultCode) {
        super(message, resultCode);
    }
}

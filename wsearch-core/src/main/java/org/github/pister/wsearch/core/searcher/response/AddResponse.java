package org.github.pister.wsearch.core.searcher.response;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午2:07
 */
public class AddResponse extends Response {
    private static final long serialVersionUID = 1592448814520951196L;

    public AddResponse() {
    }

    public AddResponse(String message, int resultCode) {
        super(message, resultCode);
    }
}

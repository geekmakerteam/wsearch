package org.github.pister.wsearch.core.searcher.response;

import org.github.pister.wsearch.core.searcher.query.OutputDocument;

import java.util.List;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午5:21
 */
public class QueryResponse extends Response {
    private static final long serialVersionUID = -285079484769384093L;

    private int totalHits;

    private List<OutputDocument> outputDocuments;

    public List<OutputDocument> getOutputDocuments() {
        return outputDocuments;
    }

    public void setOutputDocuments(List<OutputDocument> outputDocuments) {
        this.outputDocuments = outputDocuments;
    }

    public QueryResponse() {
    }

    public QueryResponse(String message, int resultCode) {
        super(message, resultCode);
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }
}

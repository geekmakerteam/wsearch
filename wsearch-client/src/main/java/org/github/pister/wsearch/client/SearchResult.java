package org.github.pister.wsearch.client;

import org.github.pister.wsearch.client.doc.Document;

import java.io.Serializable;
import java.util.List;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午9:42
 */
public class SearchResult implements Serializable {

    private static final long serialVersionUID = 7343192437667585754L;

    private int totalHits;

    private List<Document> documents;

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public int getTotalHits() {
        return totalHits;
    }
}

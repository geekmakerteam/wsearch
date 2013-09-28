package org.github.pister.wsearch.client;

import org.github.pister.wsearch.client.doc.Document;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午9:40
 */
public interface SearchClient {

    SearchResult search(SearchQuery searchQuery);

    void saveDocument(String id, Document document);

    void deleteDocument(String id);



}

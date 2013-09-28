package org.github.pister.wsearch.client;

import org.github.pister.wsearch.client.doc.Document;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午10:06
 */
public interface DumpClient {

    void beginDump();

    void addDocuments(Document[] documents);

    void finishDump();

}

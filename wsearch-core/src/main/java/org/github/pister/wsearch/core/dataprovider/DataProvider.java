package org.github.pister.wsearch.core.dataprovider;

import org.github.pister.wsearch.core.doc.InputDocument;

/**
 * User: longyi
 * Date: 13-10-6
 * Time: 下午10:38
 */
public interface DataProvider {

    void init();

    void close();

    boolean hasNext();

    InputDocument next();

}

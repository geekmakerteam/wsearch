package org.github.pister.wsearch.core.schema;

import org.apache.lucene.store.Directory;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 下午4:06
 */
public interface DataDirectory {

    Directory openDirectory();

    DataDirectory createNextDataDirectory();

    void clear();

    void saveConf();

}

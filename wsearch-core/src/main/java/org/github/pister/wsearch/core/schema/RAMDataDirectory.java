package org.github.pister.wsearch.core.schema;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 下午4:08
 */
public class RAMDataDirectory implements DataDirectory {

    @Override
    public Directory openDirectory() {
        return new RAMDirectory();
    }

    @Override
    public DataDirectory createNextDataDirectory() {
        return new RAMDataDirectory();
    }

    @Override
    public void clear() {

    }

    @Override
    public void saveConf() {

    }
}

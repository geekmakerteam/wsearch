package org.github.pister.wsearch.core.schema;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 下午4:08
 */
public class RAMSchemaMeta implements SchemaMeta {
    @Override
    public Directory openDirectory() {
        return new RAMDirectory();
    }
}

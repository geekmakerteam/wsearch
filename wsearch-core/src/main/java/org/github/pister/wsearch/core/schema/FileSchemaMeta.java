package org.github.pister.wsearch.core.schema;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 上午11:31
 */
public class FileSchemaMeta implements Serializable, SchemaMeta {
    private static final long serialVersionUID = -8995977738460544647L;

    private File basePath;
    private File dataPath;

    public FileSchemaMeta(String schemaPath) {
        basePath = new File(schemaPath);
        if (!basePath.exists()) {
            basePath.mkdirs();
        }
        dataPath = new File(basePath, "data");
        dataPath.mkdir();
    }

    @Override
    public Directory openDirectory() {
        try {
            return FSDirectory.open(dataPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

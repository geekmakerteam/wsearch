package org.github.pister.wsearch.core.schema;

import java.io.File;
import java.io.Serializable;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 上午11:31
 */
public class SchemaMeta implements Serializable {
    private static final long serialVersionUID = -8995977738460544647L;

    private File basePath;
    private File dataPath;

    public SchemaMeta(String schemaPath) {
        basePath = new File(schemaPath);
        if (!basePath.exists()) {
            basePath.mkdirs();
        }
        dataPath = new File(basePath, "data");
        dataPath.mkdir();
    }


    public File getDataPath() {
        return dataPath;
    }


}

package org.github.pister.wsearch.core.schema;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.github.pister.wsearch.core.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 上午11:31
 */
public class FileDataDirectory implements DataDirectory {

    private static final int MAX_SEQ_SIZE = 2;
    private String seqFileName = "data-seq.txt";
    private File basePath;
    private File dataPath;
    private int currentIndex;

    public FileDataDirectory(File schemaPath) {
        basePath = schemaPath;
        if (!basePath.exists()) {
            basePath.mkdirs();
        }
        currentIndex = loadGlobalSeq();
        dataPath = new File(basePath, "data-" + currentIndex);
        dataPath.mkdir();
    }

    public FileDataDirectory(File schemaPath, int currentIndex) {
        basePath = schemaPath;
        if (!basePath.exists()) {
            basePath.mkdirs();
        }
        this.currentIndex = currentIndex;
        dataPath = new File(basePath, "data-" + currentIndex);
        dataPath.mkdir();
    }

    public FileDataDirectory(String schemaPath) {
        this(new File(schemaPath));
    }

    @Override
    public synchronized void clear() {
        // 删除文件
        FileUtil.deleteFiles(dataPath);
    }

    private int loadGlobalSeq() {
        File seqFile = new File(basePath, seqFileName);
        if (!seqFile.exists()) {
            return 0;
        }
        return Integer.parseInt(FileUtil.readFromFile(seqFile).trim());
    }

    public void saveConf() {
        File seqFile = new File(basePath, seqFileName);
        FileUtil.writeToFile(String.valueOf(currentIndex), seqFile);
    }

    @Override
    public synchronized DataDirectory createNextDataDirectory() {
        int indexSeq = loadGlobalSeq();
        final int targetSeq;
        if (indexSeq >= MAX_SEQ_SIZE) {
            targetSeq = 0;
        } else {
            targetSeq = indexSeq + 1;
        }
        return new FileDataDirectory(basePath, targetSeq);
    }

    @Override
    public Directory openDirectory() {
        try {
            return FSDirectory.open(dataPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "FileDataDirectory{" +
                "basePath=" + basePath +
                ", dataPath=" + dataPath +
                '}';
    }
}

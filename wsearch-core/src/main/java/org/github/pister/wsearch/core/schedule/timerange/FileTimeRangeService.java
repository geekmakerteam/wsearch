package org.github.pister.wsearch.core.schedule.timerange;

import org.github.pister.wsearch.core.util.FileUtil;

import java.io.File;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午2:00
 */
public class FileTimeRangeService extends StringStoreTimeRangeService {

    private String filename;

    public FileTimeRangeService(String filename) {
        this.filename = filename;

        initFile();

    }

    private void initFile() {
        File file = new File(filename);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

    @Override
    protected synchronized String load() {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }
        return FileUtil.readFromFile(file);
    }

    @Override
    protected synchronized void store(String content) {
        File file = new File(filename);
        FileUtil.writeToFile(content, file);
    }
}

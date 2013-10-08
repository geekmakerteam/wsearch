package org.github.pister.wsearch.core.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午2:19
 */
public class FileUtil {

    public static void main(String[] args) {
        deleteFiles(new File("/Users/longyi/temp/d1/data-2"));
    }

    public static void deleteFiles(File path) {
        if (path == null) {
            return;
        }
        if (path.isFile()) {
            path.delete();
            return;
        }
        for (File f : path.listFiles()) {
            deleteFiles(f);
        }
        path.delete();
    }

    public static String readFromFile(File target) {
        final int BUF_LEN = 1024 * 2;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(target);
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[BUF_LEN];
            while (true) {
                int len = fileReader.read(buf, 0, BUF_LEN);
                if (len < 0) {
                    break;
                }
                sb.append(buf, 0, len);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(fileReader);
        }
    }

    public static void writeToFile(String content, File target) {
        FileWriter out = null;
        try {
            out = new FileWriter(target);
            out.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(out);
        }
    }

    public static void close(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

}

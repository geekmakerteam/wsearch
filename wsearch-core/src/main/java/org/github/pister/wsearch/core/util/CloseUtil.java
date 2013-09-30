package org.github.pister.wsearch.core.util;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

import java.io.Closeable;
import java.io.IOException;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午9:27
 */
public class CloseUtil {

    public static void close(Closeable c) {
         if (c != null) {
             try {
                 c.close();
             } catch (IOException e) {
                 // ingore
             }
         }
    }

    public static void close(IndexReader r) {
        if (r != null) {
            try {
                r.close();
            } catch (IOException e) {
                // ingore
            }
        }
    }

    public static void close(IndexWriter w) {
        if (w != null) {
            try {
                w.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static void close(IndexSearcher s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}

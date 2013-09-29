package org.github.pister.wsearch.core.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午1:54
 */
public class CollectionUtil {

    public static <T> boolean isEmpty(Collection<T> c) {
        if (c == null || c.isEmpty()) {
            return true;
        }
        return false;
    }

    public static <T> ArrayList<T> newArrayList() {
        return new ArrayList<T>();
    }

    public static <T> ArrayList<T> newArrayList(int initialCapacity) {
        return new ArrayList<T>(initialCapacity);
    }


}

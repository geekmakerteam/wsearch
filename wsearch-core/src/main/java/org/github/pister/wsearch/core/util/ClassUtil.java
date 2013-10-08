package org.github.pister.wsearch.core.util;

import org.github.pister.wsearch.core.schedule.DumpScheduleService;
import org.github.pister.wsearch.core.schema.RAMDataDirectory;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.searcher.engine.DefaultSearchEngine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午11:03
 */
public class ClassUtil {

    private static Map<Class<?>, Class<?>> primary2wraper = new HashMap<Class<?>, Class<?>>();

    static {
        primary2wraper.put(int.class, Integer.class);
        primary2wraper.put(boolean.class, Boolean.class);
        primary2wraper.put(short.class, Short.class);
        primary2wraper.put(long.class, Long.class);
        primary2wraper.put(float.class, Float.class);
        primary2wraper.put(double.class, Double.class);
    }

    public static Object newInstance(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<? extends T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<? extends T> clazz, Object[] arguments) {
        try {
            Constructor constructor = getConstructor(clazz, arguments);
            if (constructor == null) {
                throw new RuntimeException("no construct for arguments:" + stringOfArray(arguments));
            }
            return (T) constructor.newInstance(arguments);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
    }


    private static Constructor getConstructor(Class clazz, Object[] arguments) {
        constructorLoop:
        for (Constructor constructor : clazz.getConstructors()) {
            Class<?>[] types = constructor.getParameterTypes();
            if (types == null || types.length == 0) {
                if (arguments == null || arguments.length == 0) {
                    return constructor;
                }
            }
            int argumentIndex = 0;
            for (Class<?> type : types) {
                Object argument = arguments[argumentIndex++];
                if (argument == null) {
                    // null 赋值给任何类型
                    continue;
                }
                if (!isAssignableFrom(type, argument.getClass())) {
                    continue constructorLoop;
                }
            }
            return constructor;
        }
        return null;
    }

    private static boolean isAssignableFrom(Class<?> left, Class<?> right) {
        if (left.isPrimitive()) {
           if (left == right) {
               return true;
           }
           return primary2wraper.get(left) == right;
        }
        return left.isAssignableFrom(right);
    }


    public static void main(String[] args) {
        DumpScheduleService x = newInstance(DumpScheduleService.class, new Object[]{new DefaultSearchEngine(new RAMDataDirectory(), new Schema())});
        System.out.println(x);
    }

    private static String stringOfArray(Object[] array) {
        if (array == null) {
            return "<null>";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object ob : array) {
            if (first) {
                sb.append("[");
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(ob);
        }
        sb.append("]");
        return sb.toString();
    }

}

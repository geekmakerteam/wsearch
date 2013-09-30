package org.github.pister.wsearch.core.log;

import org.github.pister.wsearch.core.log.logs.JavaLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午9:39
 */
public class LoggerFactory {

    public static final Logger getLogger(String name) {
        if (isSlf4jEnable()) {
            try {
                String clazzName = "org.github.pister.wsearch.core.log.logs.Slf4jLogger";
                Class<?> slf4jClazz = Class.forName(clazzName);
                Constructor constructor = slf4jClazz.getConstructor(new Class[]{String.class});
                return (Logger)constructor.newInstance(new Object[]{name});
            } catch (ClassNotFoundException e) {
                return new JavaLogger(name);
            } catch (NoSuchMethodException e) {
                return new JavaLogger(name);
            } catch (InvocationTargetException e) {
                return new JavaLogger(name);
            } catch (InstantiationException e) {
                return new JavaLogger(name);
            } catch (IllegalAccessException e) {
                return new JavaLogger(name);
            }
        } else {
            return new JavaLogger(name);
        }
    }

    public static final Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    private static boolean isSlf4jEnable() {
        try {
            Class.forName("org.slf4j.LoggerFactory");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}

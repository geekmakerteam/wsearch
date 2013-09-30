package org.github.pister.wsearch.core.log;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午9:39
 */
public interface Logger {

    boolean isDebugEnabled();

    void debug(String msg);

    void debug(String msg, Throwable e);

    boolean isInfoEnabled();

    void info(String msg);

    void info(String msg, Throwable e);

    boolean isWarnEnabled();

    void warn(String msg);

    void warn(String msg, Throwable e);

    boolean isErrorEnabled();

    void error(String msg);

    void error(String msg, Throwable e);
}

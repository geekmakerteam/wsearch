package org.github.pister.wsearch.core.log.logs;

import org.github.pister.wsearch.core.log.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午9:53
 */
public class Slf4jLogger implements Logger {

    private org.slf4j.Logger logger;

    public Slf4jLogger(String name) {
        logger = LoggerFactory.getLogger(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void debug(String msg, Throwable e) {
        logger.debug(msg, e);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String msg, Throwable e) {
        logger.info(msg, e);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String msg, Throwable e) {
        logger.warn(msg, e);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String msg, Throwable e) {
        logger.error(msg, e);
    }
}

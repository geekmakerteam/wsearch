package org.github.pister.wsearch.core.log.logs;

import org.github.pister.wsearch.core.log.Logger;

import java.util.logging.Level;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午9:40
 */
public class JavaLogger implements Logger {

    private java.util.logging.Logger logger;

    public JavaLogger(String name) {
        logger = java.util.logging.Logger.getLogger(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.CONFIG);
    }

    @Override
    public void debug(String msg) {
        logger.log(Level.CONFIG, msg);
    }

    @Override
    public void debug(String msg, Throwable e) {
        logger.log(Level.CONFIG, msg, e);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    @Override
    public void info(String msg, Throwable e) {
        logger.log(Level.INFO, msg, e);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String msg) {
        logger.log(Level.WARNING, msg);
    }

    @Override
    public void warn(String msg, Throwable e) {
        logger.log(Level.WARNING, msg, e);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String msg) {
        logger.log(Level.SEVERE, msg);
    }

    @Override
    public void error(String msg, Throwable e) {
        logger.log(Level.SEVERE, msg, e);
    }
}

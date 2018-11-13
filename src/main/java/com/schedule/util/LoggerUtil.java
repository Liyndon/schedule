package com.schedule.util;

import org.slf4j.Logger;

/**
 * 统一日志管理
 * <p>
 * Created by fengwei.cfw on 2017/3/4.
 */
public class LoggerUtil {
    /**
     * info级别
     *
     * @param logger
     * @param format
     * @param objs
     */
    public static void info(Logger logger, String format, Object... objs) {
        if (logger.isInfoEnabled()) {
            String msg = String.format("Thread:%s：%s", Thread.currentThread().getId(), format);
            logger.info(msg, objs);
        }
    }

    /**
     * warn级别
     *
     * @param logger
     * @param format
     * @param objs
     */
    public static void warn(Logger logger, String format, Object... objs) {
        String msg = String.format("Thread:%s：%s", Thread.currentThread().getId(), format);
        logger.warn(msg, objs);
    }

    /**
     * error级别Z
     *
     * @param logger
     * @param format
     * @param objs
     */
    public static void error(Logger logger, String format, Object... objs) {
        String msg = String.format("Thread:%s：%s", Thread.currentThread().getId(), format);
        logger.error(msg, objs);
    }

    /**
     * 用于出现异常时，记录异常堆栈信息。<br/>
     * 注意，格式字符串中的占位符与slf4j一样。
     *
     * @param logger
     * @param format
     * @param t
     * @param args
     */
    public static void error(Logger logger, Throwable t, String format, Object... args) {
        String errorMsg = String.format("Thread:%s：%s", format, Thread.currentThread().getId(), args);
        logger.error(errorMsg, t);
    }

    /**
     * debug级别
     *
     * @param logger
     * @param format
     * @param objs
     */
    public static void debug(Logger logger, String format, Object... objs) {
        if (logger.isDebugEnabled()) {
            String msg = String.format("Thread:%s：%s", Thread.currentThread().getId(), format);
            logger.debug(msg, objs);
        }
    }
}

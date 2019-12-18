package com.zenatix.util;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * This is a singleton class for logging.
 * A log file will be rotated when its size reaches {@value Constant#MAX_LOG_FILE_SIZE} bytes.
 * Maximum {@value Constant#MAX_LOG_FILE_COUNT} log files can be there in {@value Constant#FIRMWARE_FOLDER_PATH} folder.
 */
public class LoggerSingleton {

    private static LoggerSingleton instance = null;

    private Logger logger = null;

    private LoggerSingleton() {
        try {
            File logDir = new File(Constant.LOG_FILE_PATH);
            if (!logDir.exists())
                logDir.mkdir();

            logger = Logger.getLogger("DfuServer");
            logger.addHandler(new FileHandler(Constant.LOG_FILE_PATH + Constant.LOG_FILE_NAME, Constant.MAX_LOG_FILE_SIZE, Constant.MAX_LOG_FILE_COUNT, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Logger getLogger() {
        return logger;
    }

    public static Logger getInstance() {
        if (instance == null)
            instance = new LoggerSingleton();

        return instance.getLogger();
    }
}

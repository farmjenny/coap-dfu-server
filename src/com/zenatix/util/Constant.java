package com.zenatix.util;

public class Constant {

    public static final String EXTENSION_DAT = ".dat";
    public static final String EXTENSION_BIN = ".bin";

    public static final int MAX_LOG_FILE_SIZE = 1024 * 1024 * 2; // 2 MB
    public static final int MAX_LOG_FILE_COUNT = 100;
//    public static final String LOG_FILE_NAME = "dfu_server%u.%g.log";
//    public static final String LOG_FILE_PATH = "~/Desktop/";

    //    public static final String THREAD_FIRMWARE_FOLDER_PATH = "~/Desktop/firmware/";
    public static final String THREAD_FIRMWARE_FOLDER_PATH = "/home/pi/sources/firmware/thread_ota_binaries/";

    public static final String LOG_FILE_NAME = "server%u.%g.log";
    public static final String LOG_FILE_PATH = "/var/log/coap_server/";
}

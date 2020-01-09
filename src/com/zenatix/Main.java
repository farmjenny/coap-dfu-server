package com.zenatix;

import com.zenatix.thread.ThreadDfuServer;
import com.zenatix.util.LoggerSingleton;

import java.net.SocketException;

public class Main {

    public static void main(String[] args) {
        try {
            LoggerSingleton.getInstance().info("Initializing thread dfu server...");
            ThreadDfuServer dfuServer = new ThreadDfuServer();
            dfuServer.start();
            LoggerSingleton.getInstance().info("Thread DFU Server started...");
        } catch (SocketException e) {
            e.printStackTrace();
            LoggerSingleton.getInstance().severe("Failed to initialize thread dfu server: " + e.getStackTrace());
        }
    }
}

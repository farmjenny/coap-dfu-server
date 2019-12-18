package com.tarunsmalviya.thread;

import com.tarunsmalviya.util.CommonMethod;
import com.tarunsmalviya.util.Constant;
import com.tarunsmalviya.util.LoggerSingleton;
import org.eclipse.californium.core.coap.CoAP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class ThreadFirmware {
    private static HashMap<String, byte[]> FILE_MAP;

    private ThreadFirmware() {
    }

    private static ArrayList<String> getFirmwareList() {
        ArrayList<String> firmware = new ArrayList<>();

        File folder = new File(Constant.THREAD_FIRMWARE_FOLDER_PATH);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile())
                firmware.add(file.getName());
        }

        return firmware;
    }

    public static byte[] getFirmware(String name, String extension) throws IOException {
        if (FILE_MAP == null)
            FILE_MAP = new HashMap<>();

        byte[] data = FILE_MAP.get(name + extension);
        if (data == null) {
            String path = getFirmwareFilePath(name, extension);
            if ((new File(path)).exists()) {
                data = Files.readAllBytes(Paths.get(path));
                FILE_MAP.put(name + extension, data);
            } else {
                LoggerSingleton.getInstance().severe("No such file " + path + " exists.");
                throw new FileNotFoundException("No such file " + path + " exists.");
            }
        }

        return data;
    }

    public static String getFirmwareFilePath(String name, String extension) {
        return Constant.THREAD_FIRMWARE_FOLDER_PATH + name + extension;
    }

    public static String isNewFirmwareAvailable(String existingFirmware) throws Exception {
        String arr[] = existingFirmware.split("\\.");
        if (arr.length != 4)
            throw new Exception("Invalid existing firmware. Valid format is <NetworkName>.<DeviceType>.<Category>.<FirmwareNumber>");


        ArrayList<String> firmwareList = getFirmwareList();
        String matchedDatName = null, matchedBinName = null;
        for (String name : firmwareList) {
            if (matchedDatName == null) {
                if (name.matches(buildFirmwareRegex(arr, Constant.EXTENSION_DAT)))
                    matchedDatName = name.replace(Constant.EXTENSION_DAT, "");
            }

            if (matchedBinName == null) {
                if (name.matches(buildFirmwareRegex(arr, Constant.EXTENSION_BIN)))
                    matchedBinName = name.replace(Constant.EXTENSION_BIN, "");
            }

            if (matchedDatName != null && matchedBinName != null)
                break;
        }

        if (matchedDatName != null && matchedBinName != null && matchedDatName.equals(matchedBinName))
            return matchedDatName;

        return existingFirmware;
    }

    public static String buildFirmwareRegex(String arr[], String extension) throws ArrayIndexOutOfBoundsException {
        if (arr.length < 3)
            throw new ArrayIndexOutOfBoundsException("Array length must be greater than or equal to 3.");

        return "^" + arr[0] + "\\." + arr[1] + "\\." + arr[2] + "\\.\\d+\\" + extension + "$";
    }
}

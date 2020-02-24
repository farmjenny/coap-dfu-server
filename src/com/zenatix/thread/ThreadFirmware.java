package com.zenatix.thread;

import com.zenatix.util.Constant;
import com.zenatix.util.LoggerSingleton;
import org.eclipse.californium.core.network.config.NetworkConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ThreadFirmware {
    private static HashMap<String, byte[]> FILE_MAP;

    private static final String FIRMWARE_PATH_CONFIG = "THREAD_FIRMWARE_FOLDER_PATH";

    private ThreadFirmware() {
    }

    private static ArrayList<String> getFirmwareList() {
        ArrayList<String> firmware = new ArrayList<>();

        String firmwarePath = NetworkConfig.getStandard().getString(ThreadFirmware.FIRMWARE_PATH_CONFIG,
                Constant.THREAD_FIRMWARE_FOLDER_PATH);

        File folder = new File(firmwarePath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    firmware.add(file.getName());
                }
            }
        } else {
            LoggerSingleton.getInstance().warning("No firmware files found at " + firmwarePath);
        }

        // Sort firmware filename by the last segment of the name descending (which should be numeric)
        Collections.sort(firmware, (String s1, String s2) -> {
            String s1Parts[] = s1.split("\\.");
            String s2Parts[] = s2.split("\\.");
            if (s1Parts.length == 5 && s2Parts.length == 5) {
                try {
                    Long ver1 = Long.valueOf(s1Parts[3]);
                    Long ver2 = Long.valueOf(s2Parts[3]);
                    return ver2.compareTo(ver1);
                } catch (NumberFormatException e) {
                    return s2Parts[3].compareTo(s1Parts[3]);
                }
            } else {
                return s2.compareTo(s1);
            }
        });

        return firmware;
    }

    public static byte[] getFirmware(String name, String extension) throws IOException {
        if (FILE_MAP == null)
            FILE_MAP = new HashMap<>();

        byte[] data = FILE_MAP.get(name + extension);
        if (data == null) {
            File path = getFirmwareFilePath(name, extension);
            final String FW_PATH = NetworkConfig.getStandard().getString(ThreadFirmware.FIRMWARE_PATH_CONFIG,
                    Constant.THREAD_FIRMWARE_FOLDER_PATH);

            // Check for path traversal
            if (!path.getCanonicalPath().startsWith(FW_PATH)) {
                LoggerSingleton.getInstance().severe("Invalid path specified: " + path);
                throw new FileNotFoundException("Bad File.");
            }
            if (path.exists()) {
                data = Files.readAllBytes(Paths.get(path.toURI()));
                FILE_MAP.put(name + extension, data);
            } else {
                LoggerSingleton.getInstance().severe("No such file " + path + " exists.");
                throw new FileNotFoundException("No such file " + path + " exists.");
            }
        }

        return data;
    }

    public static File getFirmwareFilePath(String name, String extension) {
        String firmwarePath = NetworkConfig.getStandard().getString(ThreadFirmware.FIRMWARE_PATH_CONFIG,
                Constant.THREAD_FIRMWARE_FOLDER_PATH);
        // Normalize path to prevent path traversal
        Path p = Paths.get(firmwarePath, name + extension).normalize();
        return p.toFile();
    }

    public static String isNewFirmwareAvailable(String existingFirmware) throws Exception {
        String arr[] = existingFirmware.split("\\.");
        if (arr.length != 4)
            throw new Exception(
                    "Invalid existing firmware. Valid format is <NetworkName>.<DeviceType>.<Category>.<FirmwareNumber>");

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

package com.zenatix.util;


import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class CommonMethod {

    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    public static JSONObject buildCoapPacketJsonLog(String name, CoapExchange exchange) {
        JSONObject object = new JSONObject();

        object.put("Time", getDateTime());
        object.put("Resource", name);
        if (exchange != null) {
            object.put("Source Address", exchange.getSourceAddress());
            object.put("Source Port", exchange.getSourcePort());
            object.put("Request Code", exchange.getRequestCode());
            object.put("Payload", exchange.getRequestText());
        }
        return object;
    }

    public static int getCrc(byte[] data) {
        Checksum crc = new CRC32();
        crc.update(data, 0, data.length);

        return (int) crc.getValue();
    }

    public static String bytesToHexString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}

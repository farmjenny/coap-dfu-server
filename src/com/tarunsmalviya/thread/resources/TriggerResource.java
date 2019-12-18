package com.tarunsmalviya.thread.resources;

import com.tarunsmalviya.thread.ThreadFirmware;
import com.tarunsmalviya.util.CommonMethod;
import com.tarunsmalviya.util.Constant;
import com.tarunsmalviya.util.LoggerSingleton;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class TriggerResource extends CoapResource {

    private static final String NAME = "t";

    private HashMap<String, Byte> data;

    public TriggerResource() {
        super(NAME);
        getAttributes().setTitle("t/ resource registered for GET request");

        LoggerSingleton.getInstance().info("t/ resource registered for GET request");

        data = new HashMap<>();
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        JSONObject log = CommonMethod.buildCoapPacketJsonLog(NAME, exchange);
        try {
            if (exchange != null) {
                CoAP.Type type = exchange.advanced().getRequest().getType();
                if (type == CoAP.Type.CON) {
                    String payload = exchange.getRequestText();
                    if (payload == null)
                        throw new Exception("Payload is empty.");

                    byte[] dat = ThreadFirmware.getFirmware(payload, Constant.EXTENSION_DAT);
                    byte[] bin = ThreadFirmware.getFirmware(payload, Constant.EXTENSION_BIN);

                    if (dat == null || bin == null)
                        throw new Exception("No firmware file found corresponding to name: " + payload);

                    ByteBuffer buffer = ByteBuffer.allocate(Character.BYTES + (4 * Integer.BYTES));
                    buffer.putChar((char) (1 << 4));
                    buffer.putInt(dat.length);
                    buffer.putInt(CommonMethod.getCrc(dat));
                    buffer.putInt(bin.length);
                    buffer.putInt(CommonMethod.getCrc(bin));

                    String response = CommonMethod.bytesToHexString(buffer.array());
                    response = response.replaceFirst("^0+(?!$)", "");

                    byte[] responsePayload = CommonMethod.hexStringToByteArray(response);
                    log.put("Status", "Response sent: " + responsePayload);
                    exchange.respond(CoAP.ResponseCode.CONTENT, responsePayload);
                }
            }
        } catch (Exception e) {
            log.put("Status", e.getMessage());

            if (exchange != null)
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, e.getMessage());
        }

        LoggerSingleton.getInstance().info(log.toString());
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }

    @Override
    public void handleFETCH(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }

    @Override
    public void handlePATCH(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }
}

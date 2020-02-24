package com.zenatix.thread.resources;

import com.zenatix.thread.ThreadFirmware;
import com.zenatix.util.CommonMethod;
import com.zenatix.util.Constant;
import com.zenatix.util.LoggerSingleton;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

public class ImageResource extends CoapResource {

    private static final String NAME = "f";

    public ImageResource() {
        super(NAME);
        getAttributes().setTitle("f/ resource registered for GET request");

        LoggerSingleton.getInstance().info("f/ resource registered for GET request");
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
                String payload = exchange.getRequestText();
                if (payload == null)
                    throw new Exception("Payload is empty.");

                byte[] bin = ThreadFirmware.getFirmware(payload, Constant.EXTENSION_BIN);
                if (bin == null)
                    throw new Exception(
                            "No firmware file found corresponding to name: " + payload + Constant.EXTENSION_BIN);

                log.put("Status", "Response sent: " + payload + Constant.EXTENSION_BIN + " file content.");
                exchange.respond(CoAP.ResponseCode.CONTENT, bin);

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

package com.zenatix.thread.resources;

import com.zenatix.thread.ThreadFirmware;
import com.zenatix.util.CommonMethod;
import com.zenatix.util.Constant;
import com.zenatix.util.LoggerSingleton;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

public class InitResource extends CoapResource {

    private static final String NAME = "i";

    public InitResource() {
        super(NAME);
        getAttributes().setTitle("i/ resource registered for GET request");

        LoggerSingleton.getInstance().info("i/ resource registered for GET request");
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

                byte[] dat = ThreadFirmware.getFirmware(payload, Constant.EXTENSION_DAT);
                if (dat == null)
                    throw new Exception(
                            "No firmware file found corresponding to name: " + payload + Constant.EXTENSION_DAT);

                log.put("Status", "Response sent: " + payload + Constant.EXTENSION_DAT + " file content.");
                exchange.respond(CoAP.ResponseCode.CONTENT, dat);

            }
        } catch (Exception e) {
            log.put("Status", e.getMessage());

            if (exchange != null)
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, e.getMessage());
        }

        LoggerSingleton.getInstance().info(CommonMethod.buildCoapPacketJsonLog(NAME, exchange).toString());
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

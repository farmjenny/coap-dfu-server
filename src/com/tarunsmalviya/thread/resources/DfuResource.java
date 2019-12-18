package com.tarunsmalviya.thread.resources;


import com.tarunsmalviya.thread.ThreadFirmware;
import com.tarunsmalviya.util.CommonMethod;
import com.tarunsmalviya.util.LoggerSingleton;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

public class DfuResource extends CoapResource {

    private static final String NAME = "dfu";

    public DfuResource() {
        super(NAME);
        getAttributes().setTitle("dfu/ resource registered for GET request");

        LoggerSingleton.getInstance().info("dfu/ resource registered for GET request");
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

                    String responsePayload = ThreadFirmware.isNewFirmwareAvailable(payload);
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

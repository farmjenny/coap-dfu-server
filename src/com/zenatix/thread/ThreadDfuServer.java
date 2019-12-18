package com.zenatix.thread;

import com.zenatix.thread.resources.DfuResource;
import com.zenatix.thread.resources.ImageResource;
import com.zenatix.thread.resources.InitResource;
import com.zenatix.thread.resources.TriggerResource;
import com.zenatix.util.LoggerSingleton;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class ThreadDfuServer extends CoapServer {

    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

    /**
     * Constructor in which, {@link com.zenatix.thread.resources} resources and endpoints of the server are initialized.
     *
     * @throws SocketException
     */
    public ThreadDfuServer() throws SocketException {
        LoggerSingleton.getInstance().info("Adding resources...");
        add(new DfuResource());
        add(new InitResource());
        add(new TriggerResource());
        add(new ImageResource());

        LoggerSingleton.getInstance().info("Adding endpoints...");
        addEndpoints();
    }

    /**
     * Add individual endpoints listening on default CoAP port on all IPv4 addresses of all network interfaces.
     */
    private void addEndpoints() {
        NetworkConfig config = NetworkConfig.getStandard();
        for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
            // Only binds to IPv4 addresses and localhost
            if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
                InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
                CoapEndpoint endpoint = new CoapEndpoint.Builder()
                        .setInetSocketAddress(bindToAddress)
                        .setNetworkConfig(config)
                        .build();
                addEndpoint(endpoint);
            }
        }
    }
}

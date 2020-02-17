package com.zenatix.thread;

import com.zenatix.thread.resources.DfuResource;
import com.zenatix.thread.resources.ImageResource;
import com.zenatix.thread.resources.InitResource;
import com.zenatix.thread.resources.TriggerResource;
import com.zenatix.util.LoggerSingleton;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.UdpMulticastConnector;
import org.eclipse.californium.elements.util.NetworkInterfacesUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class ThreadDfuServer extends CoapServer {

    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);// + 1;

    /** InetAddress for Mesh local multicast from FTDs */
    private static final InetAddress MULTICAST_MESH_LOCAL_FTDS_IPV6 = new InetSocketAddress("[ff03::2]", 0).getAddress();

    /**
     * Constructor in which, {@link com.zenatix.thread.resources} resources and
     * endpoints of the server are initialized.
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
        addEndpointsMulticast();
    }

    /**
     * Add individual endpoints listening on default CoAP port on all IPv4 addresses
     * of all network interfaces.
     */
    private void addEndpoints() {
        NetworkConfig config = NetworkConfig.getStandard();
        for (InetAddress addr : NetworkInterfacesUtil.getNetworkInterfaces()) {
            // Only binds to IPv4 addresses and localhost
            if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
                System.out.println(addr.toString());
                InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
                CoapEndpoint endpoint = new CoapEndpoint.Builder()
                        .setInetSocketAddress(bindToAddress)
                        .setNetworkConfig(config)
                        .build();
                addEndpoint(endpoint);
            }
        }
    }

    private void addEndpointsMulticast() {

        InetSocketAddress localAddress = new InetSocketAddress(COAP_PORT);
        Connector connector;
        if (NetworkInterfacesUtil.isAnyIpv6() && NetworkInterfacesUtil.isAnyIpv4()) {
            connector = new UdpMulticastConnector(localAddress, CoAP.MULTICAST_IPV4, CoAP.MULTICAST_IPV6_LINKLOCAL,
                    CoAP.MULTICAST_IPV6_SITELOCAL, MULTICAST_MESH_LOCAL_FTDS_IPV6);
            LoggerSingleton.getInstance().info("IPv4 & IPv6");
        } else if (NetworkInterfacesUtil.isAnyIpv6()) {
            connector = new UdpMulticastConnector(localAddress, CoAP.MULTICAST_IPV6_LINKLOCAL,
                    CoAP.MULTICAST_IPV6_SITELOCAL, MULTICAST_MESH_LOCAL_FTDS_IPV6);
            LoggerSingleton.getInstance().info("IPv6");
        } else {
            connector = new UdpMulticastConnector(localAddress, CoAP.MULTICAST_IPV4);
            LoggerSingleton.getInstance().info("IPv4");
        }
        NetworkConfig config = NetworkConfig.getStandard();

        CoapEndpoint endpoint = new CoapEndpoint.Builder().setNetworkConfig(config).setConnector(connector).build();

        addEndpoint(endpoint);
    }
}

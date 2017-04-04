package com.github.ambling;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.util.Arrays;

/**
 * An Apache Ingite server nodes participate in caching, compute execution, stream processing, etc.
 */
public class ServerNode
{
    String clusterName;
    Ignite ignite;

    public ServerNode(String clusterName, String localhost) {

        this.clusterName = clusterName;

        // Create new configuration.
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setGridName(this.clusterName);
//        cfg.setLocalHost(localhost);

        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        String[] hosts = {localhost};
        ipFinder.setAddresses(Arrays.asList(hosts));
        spi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(spi);

        ignite = Ignition.start(cfg);
    }

    public static void main( String[] args )
    {
        String clusterName = "default";
        String localhost = "localhost";
        if (args.length > 0) clusterName = args[0];
        if (args.length > 1) localhost = args[1];
        new ServerNode(clusterName, localhost);
    }
}

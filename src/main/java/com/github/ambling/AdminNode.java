package com.github.ambling;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * Apache Ingnite admin node to manage the server nodes in the cluster.
 */
public class AdminNode {

    private Ignite ignite;
    private IgniteCluster cluster;

    public AdminNode(String clusterName, Collection<String> hosts) {

        // Create new configuration.
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setGridName(clusterName);
        Ignition.setClientMode(true);
        Ignition.setDaemon(true);

        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(hosts);
        spi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(spi);

        ignite = Ignition.start(cfg);

        cluster = ignite.cluster();
    }

    void stop() {
        cluster.stopNodes();
    }

    void stop(Collection<UUID> ids) {
        cluster.stopNodes(ids);
    }

    Collection<UUID> list() {
        ArrayList<UUID> ids = new ArrayList<UUID>();
        for (ClusterNode node: cluster.nodes()) {
            ids.add(node.id());
        }
        return ids;
    }

    ClusterNode info(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return cluster.node(uuid);
        } catch (Exception e) {
            return null;
        }
    }

    void start(File config) {
        cluster.startNodes(config, false, 10, 10);
    }

    public final static String USAGE = "AdminNode [gridname] [command] <args..>\n" +
            "   list              - list available server node by ID\n" +
            "   info <ID>         - print metrics of a server node by ID\n" +
            "   start <config>    - start servers according to the config file\n" +
            "   stop              - kill all server\n" +
            "   stop <list of ID> - kill list of servers";

    public final static String COMMAND_LIST = "list";
    public final static String COMMAND_INFO = "info";
    public final static String COMMAND_START = "start";
    public final static String COMMAND_STOP = "stop";

    public static void main( String[] args )
    {
        if (args.length < 2) {
            System.out.println(USAGE);
            return;
        }

        String gridName = args[0];
        String command = args[1];

        System.out.println(gridName);

        String[] hostsArray = {"gaoDB1", "gaoDB2", "gaoDB3", "gaoDB4",
                               "gaoDB5", "gaoDB6", "gaoDB7", "gaoDB8", "gaoDB9"};
        Collection<String> hosts = Arrays.asList(hostsArray);
        AdminNode admin = new AdminNode(gridName, hosts);
        if (COMMAND_LIST.equals(command)) {
            Collection<UUID> ids = admin.list();
            for (UUID id: ids) System.out.println(id);

        } else if (COMMAND_START.equals(command)) {
            if (args.length < 3) {
                System.out.println(USAGE);
                return;
            }

            String config = args[2];
            File configFile = new File(config);
            admin.start(configFile);

        } else if (COMMAND_INFO.equals(command)) {
            if (args.length < 3) {
                System.out.println(USAGE);
                return;
            }
            String id = args[2];
            ClusterNode node = admin.info(id);
            if (node != null) {
                System.out.println("Hostname: ");
                for (String name: node.hostNames()) System.out.println("    " + name);

                ClusterMetrics metrics = node.metrics();
                System.out.println("CPU load: " + metrics.getCurrentCpuLoad());
                System.out.println("Heap memory used: " +
                        metrics.getHeapMemoryUsed() + "/" + metrics.getHeapMemoryTotal());
                System.out.println("NonHeap memory used: " +
                        metrics.getNonHeapMemoryUsed() + "/" + metrics.getNonHeapMemoryTotal());
            } else {
                System.out.println("Node dose not exist with this id: " + id);
            }

        } else if (COMMAND_STOP.equals(command)) {
            if (args.length < 3) {
                admin.stop();
            } else {
                ArrayList<UUID> ids = new ArrayList<UUID>();
                for (int i = 2; i < args.length; ++i) {
                    ClusterNode node = admin.info(args[i]);
                    if (node != null) ids.add(node.id());
                }
                admin.stop(ids);
            }
        }

        admin.ignite.close();
    }
}

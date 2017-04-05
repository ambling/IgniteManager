package com.github.ambling;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.cluster.ClusterStartNodeResult;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A manager node to manage the server nodes in the cluster.
 */
public class NodeManager {

    private Ignite ignite;
    private IgniteCluster cluster;

    public NodeManager(String configPath) {

        Ignition.setClientMode(true);
        Ignition.setDaemon(true);

        ignite = Ignition.start(configPath);

        cluster = ignite.cluster();
    }

    void stop() {
        cluster.stopNodes();
    }

    void stop(Collection<String> ids) {

        Collection<UUID> uuids = ids.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        cluster.stopNodes(uuids);
    }

    void list() {
        for (ClusterNode node: cluster.nodes()) {
            System.out.println("Node: " + node.id());
            for (String name: node.hostNames()) System.out.println("    " + name);
        }
    }

    void info(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            ClusterNode node = cluster.node(uuid);
            if (node != null) {
                System.out.println("Hostname: ");
                for (String name: node.addresses()) System.out.println("    " + name);

                ClusterMetrics metrics = node.metrics();
                System.out.println("CPU load: " + metrics.getCurrentCpuLoad());
                System.out.println("Heap memory used: " +
                        metrics.getHeapMemoryUsed() + "/" + metrics.getHeapMemoryTotal());
                System.out.println("NonHeap memory used: " +
                        metrics.getNonHeapMemoryUsed() + "/" + metrics.getNonHeapMemoryTotal());
            } else {
                System.out.println("Node dose not exist with this id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    void start(File config) {
        Collection<ClusterStartNodeResult> results = cluster.startNodes(config, false, 1000, 10);
        for (ClusterStartNodeResult result: results) {
            if (result.isSuccess()) {
                System.out.println("Start node success: " + result.getHostName());
            } else if (result.getError() != null) {
                System.out.println("Start node error: " + result.getError());
            }
        }

    }

    public final static String USAGE = "NodeManager [config] [command] <args..>\n" +
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

        String configPath = args[0];
        String command = args[1];

        NodeManager admin = new NodeManager(configPath);
        if (COMMAND_LIST.equals(command)) {
            admin.list();

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
            admin.info(id);

        } else if (COMMAND_STOP.equals(command)) {
            if (args.length < 3) {
                admin.stop();
            } else {
                Collection<String> ids = Arrays.asList(args).subList(2, args.length);
                admin.stop(ids);
            }
        }

        admin.ignite.close();
    }
}

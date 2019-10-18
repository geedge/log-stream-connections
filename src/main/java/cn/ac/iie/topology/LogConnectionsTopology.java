package cn.ac.iie.topology;


import cn.ac.iie.bolt.InsertBolt;
import cn.ac.iie.bolt.TimeCalculateBolt;
import cn.ac.iie.common.ConnectionsCountConfig;
import cn.ac.iie.spout.CustomizedKafkaSpout;
import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;


/**
 * @author Administrator
 */
public class LogConnectionsTopology {
    private static Logger logger = Logger.getLogger(LogConnectionsTopology.class);
    private final String topologyName;
    private final Config topologyConfig;
    private TopologyBuilder builder;

    private LogConnectionsTopology() {
        this(LogConnectionsTopology.class.getSimpleName());
    }

    private LogConnectionsTopology(String topologyName) {
        this.topologyName = topologyName;
        topologyConfig = createTopologConfig();
    }

    private Config createTopologConfig() {
        Config conf = new Config();
        conf.setDebug(false);
        conf.setMessageTimeoutSecs(120);
        conf.setMaxSpoutPending(ConnectionsCountConfig.TOPOLOGY_CONFIG_MAX_SPOUT_PENDING);
        conf.setNumAckers(ConnectionsCountConfig.TOPOLOGY_NUM_ACKS);
        return conf;
    }

    private void runLocally() throws InterruptedException {
        topologyConfig.setMaxTaskParallelism(1);
        StormRunner.runTopologyLocally(builder, topologyName, topologyConfig, 600);
    }

    private void runRemotely() throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
        topologyConfig.setNumWorkers(ConnectionsCountConfig.TOPOLOGY_WORKERS);
        //设置过高会导致很多问题，如心跳线程饿死、吞吐量大幅下跌
        topologyConfig.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 8);
        StormRunner.runTopologyRemotely(builder, topologyName, topologyConfig);
    }

    private void buildTopology() {
        builder = new TopologyBuilder();
        builder.setSpout("CustomizedKafkaSpout", new CustomizedKafkaSpout(), ConnectionsCountConfig.SPOUT_PARALLELISM);
        builder.setBolt("TimeCalculateBolt", new TimeCalculateBolt(), ConnectionsCountConfig.TIME_CALCULATE_BOLT_PARALLELISM).localOrShuffleGrouping("CustomizedKafkaSpout");
        builder.setBolt("InsertBolt", new InsertBolt(), 1).localOrShuffleGrouping("TimeCalculateBolt");

    }

    public static void main(String[] args) throws Exception {
        LogConnectionsTopology logConnectionsTopology;
        boolean runLocally = true;
        String command = "remote";
        if (args.length >= 2 && command.equalsIgnoreCase(args[1])) {
            runLocally = false;
            logConnectionsTopology = new LogConnectionsTopology(args[0]);
        } else {
            logConnectionsTopology = new LogConnectionsTopology();
        }
        logConnectionsTopology.buildTopology();

        if (runLocally) {
            logger.info("执行本地模式...");
            logConnectionsTopology.runLocally();
        } else {
            logger.info("执行远程部署模式...");
            logConnectionsTopology.runRemotely();
        }
    }
}

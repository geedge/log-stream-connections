package cn.ac.iie.common;

import cn.ac.iie.utils.system.ConnectionsCountConfigurations;

import java.io.Serializable;

/**
 * @author qidaijie
 */
public class ConnectionsCountConfig implements Serializable {

    private static final long serialVersionUID = -8649024767966235184L;
    public static final String LOG_STRING_SPLITTER = ",";
    public static final String EMPTY_OPTION_CHARACTER = "-";

    public static final Integer MAX_NUMBER = ConnectionsCountConfigurations.getIntProperty(1, "max.number");
    public static final Integer SPOUT_PARALLELISM = ConnectionsCountConfigurations.getIntProperty(1, "topology.spout.parallelism");
    public static final String RESULT_SOUTPUT_TOPIC = ConnectionsCountConfigurations.getStringProperty(1, "results.output.topics");

    public static final Integer TOPOLOGY_CONFIG_MAX_SPOUT_PENDING = ConnectionsCountConfigurations.getIntProperty(1, "topology.config.max.spout.pending");
    public static final Integer TOPOLOGY_NUM_ACKS = ConnectionsCountConfigurations.getIntProperty(1, "topology.num.acks");
    public static final Integer TIME_CALCULATE_BOLT_PARALLELISM = ConnectionsCountConfigurations.getIntProperty(1, "topology.bolt.time.calculate");
    public static final Integer MAX_FAILURE_NUM = ConnectionsCountConfigurations.getIntProperty(1, "max.failure.num");

    public static final Integer TOPOLOGY_TICK_TUPLE_FREQ_SECS = ConnectionsCountConfigurations.getIntProperty(1, "topology.tick.tuple.freq.secs");
    /**
     * Kafka
     */
    public static final String GROUP_ID = ConnectionsCountConfigurations.getStringProperty(1, "group.id");
    public static final String KAFKA_TOPIC = ConnectionsCountConfigurations.getStringProperty(1, "kafka.topic");

    public static final Integer TOPOLOGY_WORKERS = ConnectionsCountConfigurations.getIntProperty(1, "topology.workers");
    public static final String BOOTSTRAP_SERVERS = ConnectionsCountConfigurations.getStringProperty(1, "bootstrap.servers");
    public static final String RESULTS_OUTPUT_SERVERS = ConnectionsCountConfigurations.getStringProperty(1, "results.output.servers");
    public static final String AUTO_OFFSET_RESET = ConnectionsCountConfigurations.getStringProperty(1, "auto.offset.reset");


}
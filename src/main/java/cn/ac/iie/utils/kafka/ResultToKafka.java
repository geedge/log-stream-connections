package cn.ac.iie.utils.kafka;

import cn.ac.iie.bean.ConnectionTime;
import cn.ac.iie.common.ConnectionsCountConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zdjizhi.utils.StringUtil;
import org.apache.kafka.clients.producer.*;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * 结果日志写入数据中心类
 *
 * @author Administrator
 * @create 2018-08-13 15:11
 */

public class ResultToKafka {
    private static Logger logger = Logger.getLogger(ResultToKafka.class);

    /**
     * kafka生产者，用于向kafka中发送消息
     */
    private static Producer<String, String> kafkaProducer;

    /**
     * kafka生产者适配器（单例），用来代理kafka生产者发送消息
     */
    private static ResultToKafka resultToKafka;

    private ResultToKafka() {
        initKafkaProducer();
    }

    public static ResultToKafka getInstance() {
        if (resultToKafka == null) {
            resultToKafka = new ResultToKafka();
        }
        return resultToKafka;
    }


    public void sendMessage(String jsons) {
        final int[] errorSum = {0};
        JSONArray jsonArray = JSON.parseArray(jsons);
        for (int i = 0; i < jsonArray.size(); i++) {
            String message = jsonArray.getString(i);
            if (StringUtil.isNotBlank(message)) {
                kafkaProducer.send(new ProducerRecord<>(ConnectionsCountConfig.RESULT_SOUTPUT_TOPIC, message), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            logger.error("发送kafka出现异常", exception);
                            errorSum[0]++;
                        }
                    }
                });
            }
            if (errorSum[0] >= 20) {
                return;
            }
        }
        kafkaProducer.flush();
    }

    /**
     * 根据kafka生产者配置信息初始化kafka消息生产者,只初始化一次
     */
    private void initKafkaProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", ConnectionsCountConfig.RESULTS_OUTPUT_SERVERS);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("acks", "1");
        properties.put("linger.ms", "2");
        properties.put("request.timeout.ms", 20000);
        properties.put("batch.size", 262144);
        properties.put("buffer.memory", 33554432);
        kafkaProducer = new KafkaProducer<>(properties);
    }

}

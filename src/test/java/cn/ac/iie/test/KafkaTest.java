package cn.ac.iie.test;

import cn.ac.iie.common.ConnectionsCountConfig;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class KafkaTest {

    public static void main(String[] args) {
        Properties properties = new Properties();
//        properties.put("bootstrap.servers", ConnectionsCountConfig.BOOTSTRAP_SERVERS);
        properties.put("bootstrap.servers", "192.168.10.207:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("acks", "1");
        properties.put("linger.ms", "2");
        properties.put("request.timeout.ms", 20000);
        properties.put("batch.size", 262144);
        properties.put("buffer.memory", 33554432);
        Producer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        UrlsBean urlsBean = new UrlsBean();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10 - i; j++) {
                urlsBean.setUrl("https://lixiaohui.iteye.com/blog/bbbb/" + i);
                urlsBean.setServer_ip("192.161.10." + j);
                urlsBean.setClient_ip("119.72.160." + j);
                urlsBean.setC2s_byte_num(1000);
                urlsBean.setC2s_pkt_num(1000);
                urlsBean.setS2c_byte_num(1000);
                urlsBean.setS2c_pkt_num(1000);
                kafkaProducer.send(new ProducerRecord<>("JsonTest", JSONObject.toJSONString(urlsBean)), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if (e != null){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        kafkaProducer.flush();
    }


}

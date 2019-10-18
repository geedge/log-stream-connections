package cn.ac.iie.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.BitSet;

public class JsonTest {

    @Test
    public void nullKeyTest() {
        try {
            String message = "{\"stream_trace_id\":\"11570861088.164491556\",\"policy_id\":-1,\"action\":0,\"service\":0,\"start_time\":1570861088,\"addr_type\":4,\"client_ip\":\"92.47.150.222\",\"server_ip\":\"203.205.142.154\",\"client_port\":59253,\"server_port\":80,\"trans_proto\":\"IPv4_TCP\",\"entrance_id\":0,\"device_id\":0,\"link_id\":0,\"isp\":\"\",\"encap_type\":0,\"pinningst\":0,\"intercept_state\":0,\"ssl_server_side_latency\":0,\"ssl_client_side_latency\":0,\"ssl_server_side_version\":\"\",\"ssl_client_side_version\":\"\",\"direction\":0,\"stream_dir\":3,\"cap_ip\":\"10.4.39.21\",\"addr_list\":\"\",\"c2s_pkt_num\":4,\"s2c_pkt_num\":2,\"c2s_byte_num\":0,\"s2c_byte_num\":0,\"has_dup_traffic\":0,\"stream_error\":\"e_no_data\"}";
            JSONObject jsonObject = JSON.parseObject(message);
            int startTime = 0;
            int endTime = 0;
            if (message.contains("start_time") && message.contains("end_time")) {
                startTime = jsonObject.getInteger("start_time");
                endTime = jsonObject.getInteger("end_time");
            }
            System.out.println("starTime:" + startTime + "\tendTime:" + endTime);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

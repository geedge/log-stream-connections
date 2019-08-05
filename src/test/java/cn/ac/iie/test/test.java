package cn.ac.iie.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zdjizhi.utils.StringUtil;

import java.util.BitSet;

public class test {

    public static void main(String[] args) {
//        String date = "w_conn_num\":47950,\"stat_time\":1564134300}, {\"close_conn_num\":179047,\"live_conn_num\":168052,\"new_conn_num\":10995,\"stat_time\":1564133910}, {\"close_conn_num\":224605,\"live_conn_num\":323861,\"new_conn_num\":156273,\"stat_time\":1564133880}, {\"close_conn_num\":0,\"live_conn_num\":236384,\"new_conn_num\":190984,\"stat_time\":1564133850}, {\"close_conn_num\":0,\"live_conn_num\":45400,\"new_conn_num\":45400,\"stat_time\":1564133820}]\n";
//        String date = "[{\"close_conn_num\":981,\"live_conn_num\":981,\"new_conn_num\":247,\"stat_time\":1564471920}, {\"close_conn_num\":981,\"live_conn_num\":981,\"new_conn_num\":247,\"stat_time\":1564471920}, {\"close_conn_num\":0,\"live_conn_num\":741,\"new_conn_num\":487,\"stat_time\":1564471890}, {\"close_conn_num\":0,\"live_conn_num\":741,\"new_conn_num\":487,\"stat_time\":1564471890}, {\"close_conn_num\":0,\"live_conn_num\":255,\"new_conn_num\":256,\"stat_time\":1564471860}]\n";
        String date = "[null, null, null, null, null, {\"close_conn_num\":0,\"live_conn_num\":166,\"new_conn_num\":165,\"stat_time\":1564477980}]";
        JSONArray jsonArray = JSON.parseArray(date);
        for (int i = 0; i < jsonArray.size(); i++) {
            String string = jsonArray.getString(i);
            if (StringUtil.isNotBlank(string)){

                System.out.println(string);
            }
        }
    }
}

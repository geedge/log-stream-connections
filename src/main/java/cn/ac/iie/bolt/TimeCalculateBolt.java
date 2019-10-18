package cn.ac.iie.bolt;

import cn.ac.iie.bean.ConnectionTime;
import cn.ac.iie.common.ConnectionsCountConfig;
import cn.ac.iie.utils.TimeUtils;
import cn.ac.iie.utils.system.TupleUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.*;

import static cn.ac.iie.utils.TimeUtils.getNowTime;

/**
 * 开始统计：1:5:00
 * 入库时间：1:5:30 对应时间戳 1558371930
 * <p>
 * 1:5:00-1:5:30 1:4:30-1:5:00 1:4:00-1:4:30 1:3:30-1:4:00 1:3:00-1:3:30 1:2:30-1:3:00 1:2:00-1:2:30 1:1:30-1:2:00 1:1:00-1:1:30 1:0:30-1:1:00
 * 0		         1			   2		    3			  4		         5			   6		     7			   8		    9
 * <p>
 * 例如日志中的开始结束时间如下：
 * <p>
 * start：1:02:00 对应时间戳 1558371720  -> (1558371930 - 1558371720) / 维度(30) = 7区间 对应value+1
 * end：1:04:00 对应时间戳 1558371840  -> (1558371930 - 1558371840) / 维度(30) = 3区间 对应value+1
 * active：新建-结束之间 -> 4 5 6区间 对应value+1
 * <p>
 * 程序启动获取当前时间并前推取整，向前分10个分区，对开始和结束时间进行计算累加，30S再次获取当前时间。
 *
 * @author qidaijie
 */

public class TimeCalculateBolt extends BaseBasicBolt {
    private static final long serialVersionUID = -6628202735196670575L;
    private static Logger logger = Logger.getLogger(TimeCalculateBolt.class);
    private static Long nowTime;
    private static Map<Integer, Long> startMap;
    private static Map<Integer, Long> endMap;
    private static Map<Integer, Long> intervalMap;
    private static List<String> list;


    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        nowTime = getNowTime();
        startMap = new HashMap<>(ConnectionsCountConfig.MAX_NUMBER);
        endMap = new HashMap<>(ConnectionsCountConfig.MAX_NUMBER);
        intervalMap = new HashMap<>(ConnectionsCountConfig.MAX_NUMBER);
        list = new ArrayList<>();
        for (int i = 0; i < ConnectionsCountConfig.MAX_NUMBER; i++) {
            startMap.put(i, 0L);
            endMap.put(i, 0L);
            intervalMap.put(i, 0L);
        }
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        try {
            if (TupleUtils.isTick(tuple)) {
                List<String> result = new ArrayList<>(getResult(nowTime));
                list.clear();
                if (result.size() > 0) {
                    basicOutputCollector.emit(new Values(result.toString()));
                }
                nowTime = getNowTime();
            } else {
                String message = tuple.getString(0);
                JSONObject jsonObject = JSON.parseObject(message);
                if (message.contains("start_time") && message.contains("end_time")) {
                    int startTime = jsonObject.getInteger("start_time");
                    int endTime = jsonObject.getInteger("end_time");
                    if (startTime != 0 && endTime != 0) {
                        int newConnDifference = TimeUtils.getTimeBetween(nowTime, startTime);
                        int endConnDifference = TimeUtils.getTimeBetween(nowTime, endTime);
//                    System.out.println(newConnDifference + "\t\t\t" + endConnDifference);
                        timeIntervalCount(newConnDifference, endConnDifference);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("解析和写入kafka出现异常");
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<>(16);
        conf.put(org.apache.storm.Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, ConnectionsCountConfig.TOPOLOGY_TICK_TUPLE_FREQ_SECS);
        return conf;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("value"));
    }

    /**
     * 对时间间隔进行计算，间隔内的数据进行累加等
     *
     * @param newConnDifference 新建连接时间差
     * @param endConnDifference 关闭连接时间差
     */
    private static void timeIntervalCount(int newConnDifference, int endConnDifference) {
        try {
            boolean start = false;
            boolean end = false;
            if (newConnDifference >= 0 && newConnDifference < ConnectionsCountConfig.MAX_NUMBER) {
                startMap.put(newConnDifference, startMap.get(newConnDifference) + 1);
                start = true;
            }

            if (endConnDifference >= 0 && endConnDifference < ConnectionsCountConfig.MAX_NUMBER) {
                endMap.put(endConnDifference, endMap.get(endConnDifference) + 1);
                end = true;
            }

            if (start && end) {
                if (newConnDifference == 0 && endConnDifference == 0) {
                    intervalMap.put(0, intervalMap.get(0) + 1);
                } else if (newConnDifference - endConnDifference > 0) {
                    for (int i = endConnDifference; i <= newConnDifference; i++) {
                        intervalMap.put(i, intervalMap.get(i) + 1);
                    }
                }
            } else if (!start && end) {
                for (int i = ConnectionsCountConfig.MAX_NUMBER - 1; i >= endConnDifference; i--) {
                    intervalMap.put(i, intervalMap.get(i) + 1);
                }
            }

        } catch (Exception e) {
            logger.error("错误的开始和结束" + newConnDifference + endConnDifference);
        }
    }

    /**
     * 获取结果集传入下一bolt
     *
     * @return 统计结果集
     */
    private static List<String> getResult(Long nowTime) {
        ConnectionTime connectionTime = new ConnectionTime();
        for (int key : startMap.keySet()) {
            if (startMap.get(key) == 0 && intervalMap.get(key) == 0 && endMap.get(key) == 0) {
//                logger.info("结果均为0不予以入库");
            } else {
                connectionTime.setNew_conn_num(startMap.get(key));
                connectionTime.setLive_conn_num(intervalMap.get(key));
                connectionTime.setClose_conn_num(endMap.get(key));
                connectionTime.setStat_time(nowTime - 30 * key);
                list.add(JSONObject.toJSONString(connectionTime));
                startMap.put(key, 0L);
                endMap.put(key, 0L);
                intervalMap.put(key, 0L);
            }
        }
        return list;
    }


}

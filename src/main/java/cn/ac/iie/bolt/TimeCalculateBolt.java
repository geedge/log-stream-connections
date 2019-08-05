package cn.ac.iie.bolt;

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
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.ac.iie.utils.Statistical.*;
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


    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        nowTime = getNowTime();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        try {
            if (TupleUtils.isTick(tuple)) {
                List<String> result = getResult(nowTime);
                if (result.size() > 0) {
                    basicOutputCollector.emit(new Values(getResult(nowTime).toString()));
                    clear();
                }
                nowTime = getNowTime();
            } else {
                String message = tuple.getString(0);
                JSONObject jsonObject = JSON.parseObject(message);
                int startTime = jsonObject.getInteger("start_time");
                int endTime = jsonObject.getInteger("end_time");
                if (startTime != 0 && endTime != 0) {
                    int newConnDifference = TimeUtils.getTimeBetween(nowTime, startTime);
                    int endConnDifference = TimeUtils.getTimeBetween(nowTime, endTime);
//                    System.out.println(newConnDifference + "\t\t\t" + endConnDifference);
                    timeIntervalCount(newConnDifference, endConnDifference);
                }
            }
        } catch (Exception e) {
            logger.error("解析和写入kafka出现异常", e);
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

}

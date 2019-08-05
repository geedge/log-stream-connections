package cn.ac.iie.utils;

import cn.ac.iie.bean.ConnectionTime;
import cn.ac.iie.common.ConnectionsCountConfig;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qidaijie
 */
public class Statistical {
    private static final Logger logger = Logger.getLogger(Statistical.class);

    private static Map<Integer, Long> startMap = new HashMap<>(ConnectionsCountConfig.MAX_NUMBER);

    private static Map<Integer, Long> endMap = new HashMap<>(ConnectionsCountConfig.MAX_NUMBER);

    private static Map<Integer, Long> intervalMap = new HashMap<>(ConnectionsCountConfig.MAX_NUMBER);

    private static List<String> list = new LinkedList<>();

    static {
        for (int i = 0; i < ConnectionsCountConfig.MAX_NUMBER; i++) {
            startMap.put(i, 0L);
            endMap.put(i, 0L);
            intervalMap.put(i, 0L);
        }
    }

    /**
     * 对时间间隔进行计算，间隔内的数据进行累加等
     *
     * @param newConnDifference 新建连接时间差
     * @param endConnDifference 关闭连接时间差
     */
    public static void timeIntervalCount(int newConnDifference, int endConnDifference) {
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
    public static List<String> getResult(Long nowTime) {
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

    /**
     * clear this list
     */
    public static void clear() {
        list.clear();
    }


}

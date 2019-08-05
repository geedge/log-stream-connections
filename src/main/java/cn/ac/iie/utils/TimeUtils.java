package cn.ac.iie.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author qidaijie
 */
public class TimeUtils {

    /**
     * @param nowTime 现在时间
     * @param logTime 日志时间
     * @return 时间区间
     */
    public static int getTimeBetween(Long nowTime, int logTime) {
        long between = nowTime - logTime;
        int dimension = 30;
        return (int) (between / dimension);
    }

    /**
     * 获取现在时间戳并向左取整
     *
     * @return 时间戳秒级
     */
    public static Long getNowTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        String substring = format.substring(0, format.length() - 2);
        String time;
        Long ts = 0L;
        if (Integer.parseInt(format.split(":")[2]) > 30) {
            time = substring + "30";
        } else {
            time = substring + "00";
        }
        try {
            Date parse = simpleDateFormat.parse(time);
            ts = parse.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(ts + 30);
        return ts + 30;
    }

}

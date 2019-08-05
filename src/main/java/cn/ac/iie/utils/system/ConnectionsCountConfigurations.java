package cn.ac.iie.utils.system;

import java.util.Properties;


/**
 * @author qidaijie
 */
public final class ConnectionsCountConfigurations {

    private static Properties propService = new Properties();

    public static String getStringProperty(Integer type, String key) {
        if (type == 1) {
            return propService.getProperty(key);
        } else {
            return null;
        }
    }

    public static Integer getIntProperty(Integer type, String key) {
        if (type == 1) {
            return Integer.parseInt(propService.getProperty(key));
        } else {
            return null;
        }
    }

    public static Long getLongProperty(Integer type, String key) {
        if (type == 1) {
            return Long.parseLong(propService.getProperty(key));
        } else {
            return null;
        }
    }

    public static Boolean getBooleanProperty(Integer type, String key) {
        if (type == 1) {
            return "true".equals(propService.getProperty(key).toLowerCase().trim());
        } else {
            return null;
        }
    }

    static {
        try {
            propService.load(ConnectionsCountConfigurations.class.getClassLoader().getResourceAsStream("kafka_topic.properties"));
            System.out.println("kafka_topic.properties加载成功");
        } catch (Exception e) {
            propService = null;
            System.err.println("Configuration loading failed!!");
        }
    }
}

package cn.ac.iie.bean;

/**
 * @author qidaijie
 */
public class ConnectionTime {
    private Long new_conn_num;
    private Long live_conn_num;
    private Long close_conn_num;
    private Long stat_time;

    public ConnectionTime() {
    }

    public Long getNew_conn_num() {
        return new_conn_num;
    }

    public void setNew_conn_num(Long new_conn_num) {
        this.new_conn_num = new_conn_num;
    }

    public Long getLive_conn_num() {
        return live_conn_num;
    }

    public void setLive_conn_num(Long live_conn_num) {
        this.live_conn_num = live_conn_num;
    }

    public Long getClose_conn_num() {
        return close_conn_num;
    }

    public void setClose_conn_num(Long close_conn_num) {
        this.close_conn_num = close_conn_num;
    }

    public Long getStat_time() {
        return stat_time;
    }

    public void setStat_time(Long stat_time) {
        this.stat_time = stat_time;
    }
}

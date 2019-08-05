package cn.ac.iie.test;

public class UrlsBean {
    private String url;
    private String server_ip;
    private String client_ip;
    private int c2s_byte_num;
    private int s2c_byte_num;
    private int c2s_pkt_num;
    private int s2c_pkt_num;

    public UrlsBean() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public int getC2s_byte_num() {
        return c2s_byte_num;
    }

    public void setC2s_byte_num(int c2s_byte_num) {
        this.c2s_byte_num = c2s_byte_num;
    }

    public int getS2c_byte_num() {
        return s2c_byte_num;
    }

    public void setS2c_byte_num(int s2c_byte_num) {
        this.s2c_byte_num = s2c_byte_num;
    }

    public int getC2s_pkt_num() {
        return c2s_pkt_num;
    }

    public void setC2s_pkt_num(int c2s_pkt_num) {
        this.c2s_pkt_num = c2s_pkt_num;
    }

    public int getS2c_pkt_num() {
        return s2c_pkt_num;
    }

    public void setS2c_pkt_num(int s2c_pkt_num) {
        this.s2c_pkt_num = s2c_pkt_num;
    }
}

package com.xgit.kafka.bean;

/**
 * @program: KafkaMonitoring
 * @description: brokers
 * @author: Mr.Wang
 * @create: 2019-11-26 10:13
 **/
public class Brokers {
    /**
     * 编号
     */
    private int id;

    /**
     * 地址
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

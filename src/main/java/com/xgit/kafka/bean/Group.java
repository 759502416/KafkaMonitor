package com.xgit.kafka.bean;

import java.util.List;

/**
 * @program: KafkaMonitoring
 * @description: 消费者组信息
 * @author: Mr.Wang
 * @create: 2019-11-26 10:20
 **/
public class Group {

    /**
     * Kafkfa的Broker组
     */
    private List<Brokers> brokers;

    /**
     * group下的消费者组描述信息
     */
    private List<Offsets> offsets;

    public List<Brokers> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<Brokers> brokers) {
        this.brokers = brokers;
    }

    public List<Offsets> getOffsets() {
        return offsets;
    }

    public void setOffsets(List<Offsets> offsets) {
        this.offsets = offsets;
    }
}

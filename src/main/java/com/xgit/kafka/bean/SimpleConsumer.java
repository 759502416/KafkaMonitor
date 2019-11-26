package com.xgit.kafka.bean;

import java.io.Serializable;

/**
 * @program: KafkaMonitoring
 * @description: 简单明了的消费信息
 * @author: Mr.Wang
 * @create: 2019-11-26 15:52
 **/
public class SimpleConsumer implements Serializable {
    /**
     * 分组名称
     */
    private String group;
    /**
     * Topic名称
     */
    private String topic;
    /**
     * Topic各分区的总LogSize
     */
    private Long totalLogSize = 0L;
    /**
     * Group的总消费偏移量
     */
    private Long totalOffset = 0L;
    /**
     * Topic的消费堆积量
     */
    private Long overstockOffset = 0L;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getTotalLogSize() {
        return totalLogSize;
    }

    public void setTotalLogSize(Long totalLogSize) {
        this.totalLogSize = totalLogSize;
    }

    public Long getTotalOffset() {
        return totalOffset;
    }

    public void setTotalOffset(Long totalOffset) {
        this.totalOffset = totalOffset;
    }

    public Long getOverstockOffset() {
        return overstockOffset;
    }

    public void setOverstockOffset(Long overstockOffset) {
        this.overstockOffset = overstockOffset;
    }
}

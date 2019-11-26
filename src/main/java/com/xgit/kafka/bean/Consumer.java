package com.xgit.kafka.bean;

/**
 * @program: KafkaMonitoring
 * @description: Group消费者
 * @author: Mr.Wang
 * @create: 2019-11-26 14:53
 **/
public class Consumer {
    /**
     * 消费者组的名称
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

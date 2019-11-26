package com.xgit.kafka.bean;

import java.util.ArrayList;

/**
 * @program: KafkaMonitoring
 * @description: Topic的消费者组信息
 * @author: Mr.Wang
 * @create: 2019-11-26 14:54
 **/
public class ConsumerForTopic {

    private ArrayList<Consumer> consumers;

    public ArrayList<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(ArrayList<Consumer> consumers) {
        this.consumers = consumers;
    }
}

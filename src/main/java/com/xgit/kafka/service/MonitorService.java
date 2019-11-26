package com.xgit.kafka.service;

import org.springframework.stereotype.Service;

/**
 * @program: KafkaMonitoring
 * @description: 监控抽象服务层
 * @author: Mr.Wang
 * @create: 2019-11-26 09:38
 **/
public interface MonitorService {

    /**
     * 获取所有的消费组
     * @return
     */
    String getAllGroup(String groupName);

    /**
     * 获取所有的topic
     * @return
     */
    String getAllTopicList();

    /**
     * 获取一个Topic下所有的Group的列表
     * @param topicName
     * @return
     */
    String getAllGroupFromOneTopic(String topicName);


    /**
     * 给定group和topic，得到消费监控
     * @param group
     * @param topic
     * @return
     */
    String getSimpleDataWithGroupAndTopic(String group,String topic);


}

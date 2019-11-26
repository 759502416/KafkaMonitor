package com.xgit.kafka.impl;

import com.alibaba.fastjson.JSONObject;
import com.xgit.kafka.bean.*;
import com.xgit.kafka.monitor.KafkaUtil;
import com.xgit.kafka.service.MonitorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: KafkaMonitoring
 * @description: Kafka监控服务实现
 * @author: Mr.Wang
 * @create: 2019-11-26 09:52
 **/
@Service
public class MonitorServiceImpl implements MonitorService {


    @Override
    public String getAllGroup(String groupName) {
        List<String> allGroups = KafkaUtil.getAllGroups();
        if (null == groupName){
            return JSONObject.toJSONString(allGroups);
        } else {
            try {
                Group group = new Group();
                // 如果传递过来的group是无效group，则返回一个空的
                if(!allGroups.contains(groupName)){
                    group.setOffsets(new ArrayList<>());
                    group.setBrokers(new ArrayList<>());
                    return JSONObject.toJSONString(group);
                }
                List<Brokers> brokers = KafkaUtil.brokers;
                // 设置brokers
                group.setBrokers(brokers);
                // 创建一个 ArrayList<Offsets>载体
                ArrayList<Offsets> allOffsets = new ArrayList<>();
                // 获取该Group对应的所有Topic
                List<String> allTopicFromGroup = KafkaUtil.getAllTopicFromGroup(groupName);
                allTopicFromGroup.forEach(topic->{
                    ArrayList<Offsets> offsets = KafkaUtil.getLagByGroupAndTopicWithPartition(groupName, topic);
                    allOffsets.addAll(offsets);
                });
                // 设置offsets
                group.setOffsets(allOffsets);
                return JSONObject.toJSONString(group);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getAllTopicList() {
        List<String> allTopics = KafkaUtil.getAllTopics();
        return JSONObject.toJSONString(allTopics);
    }

    @Override
    public String getAllGroupFromOneTopic(String topicName) {
        List<String> groups = KafkaUtil.getOneTopicAllGroup(topicName);
        ArrayList<Consumer> consumers = new ArrayList<>();
        groups.forEach(group->{
            Consumer consumer = new Consumer();
            consumer.setName(group);
            consumers.add(consumer);
        });
        ConsumerForTopic consumerForTopic = new ConsumerForTopic();
        consumerForTopic.setConsumers(consumers);
        return JSONObject.toJSONString(consumerForTopic) ;
    }

    @Override
    public String getSimpleDataWithGroupAndTopic(String group, String topic) {
        ArrayList<Offsets> offsets = KafkaUtil.getLagByGroupAndTopicWithPartition(group, topic);
        SimpleConsumer simpleConsumer = new SimpleConsumer();
        for (int i = 0; i <offsets.size(); i++) {
            Offsets offse = offsets.get(i);
            simpleConsumer.setTotalLogSize(simpleConsumer.getTotalLogSize()+offse.getLogSize());
            simpleConsumer.setTotalOffset(simpleConsumer.getTotalOffset()+offse.getOffset());
            simpleConsumer.setOverstockOffset(simpleConsumer.getOverstockOffset()+offse.getLogSize()-offse.getOffset());
        }
        simpleConsumer.setGroup(group);
        simpleConsumer.setTopic(topic);
        return JSONObject.toJSONString(simpleConsumer);
    }
}

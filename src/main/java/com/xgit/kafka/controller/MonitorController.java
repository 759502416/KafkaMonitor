package com.xgit.kafka.controller;

import com.xgit.kafka.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: KafkaMonitoring
 * @description: 监控控制器
 * @author: Mr.Wang
 * @create: 2019-11-26 09:29
 **/
@RestController
public class MonitorController {

    @Autowired
    private MonitorService MonitorServiceImple;

    /**
     * 获取所有的Topic列表
     * @param
     * @return
     */
    @RequestMapping("/topiclist")
    public String getTopicList(){
        return MonitorServiceImple.getAllTopicList();
    }

    /**
     * 获取指定的Topic的消费者组 的 信息
     * @param groupName
     * @return
     */
    @RequestMapping("/topicdetails/{groupName}")
    public String getGroupDetails(@PathVariable String groupName){
        return MonitorServiceImple.getAllGroupFromOneTopic(groupName);
    }


    /**
     * 获取特定groupId的所有信息
     * @return
     */
    @RequestMapping("/group/{groupId}")
    public String getAllGroup(@PathVariable String groupId){
        return MonitorServiceImple.getAllGroup(groupId);
    }

    /**
     * 获取group的所有信息  不传groupId
     * @return
     */
    @RequestMapping("/group")
    public String getAllGroup(){
        return MonitorServiceImple.getAllGroup(null);
    }

    /**
     * 给定group和topic，得到消费监控
     * @param group
     * @param topic
     * @return
     */
    @RequestMapping("/simple/{topic}/{group}")
    public String getSimpleData(@PathVariable String topic,@PathVariable String group){
        return MonitorServiceImple.getSimpleDataWithGroupAndTopic(group, topic);
    }
}

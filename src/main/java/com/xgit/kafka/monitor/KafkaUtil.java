package com.xgit.kafka.monitor;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @program: KafkaMonitoring
 * @description: kafka的工具类
 * @author: Mr.Wang
 * @create: 2019-11-25 18:19
 **/
public class KafkaUtil {
    /**
     * 这是kafka的Broker_server   也可以搞成前端来配
     */
    private static final String BROKER_SERVERS = "hdp-3.hadoop:6667,hdp-4.hadoop:6667";
    public static void main(String[] args) throws Exception {
        // 接口1 获取所有的Topic
        List<String> allTopics = getAllTopics();
        // 接口2 获得所有的Group
        List<String> allGroups = getAllGroups();

        // 接口3  根据给定的GroupId，类似我下面的这种group，获得所有它的Topic。
        List<String> allTopicFromGroup = getAllTopicFromGroup(allGroups.get(0));
        //Group   先写死测试
        String group ="XGIT_ENV_XM2M_RAW_HDFS";
        //Topic
        String topic ="XGIT_ENV_XM2M_RAWDATA";


        // 这里正常情况是是用定时调度器，此处测试工具图方便而已
        while (true){
            Thread.sleep(3000);
            // 获得当前对饮 Group的Topic的offset
            Long totalTopicOffset = getLagByGroupAndTopic(group, topic);
            // 获得当前对应
            Long consumerOffsetForTopic = getGroupsForTopic(group, topic);
            System.err.println("Group:"+group+",Topic:"+topic+",TotalOffset:"+totalTopicOffset+",NowOffset:"+
                    consumerOffsetForTopic+",overstockOffset:"+(totalTopicOffset-consumerOffsetForTopic));
        }

    }



    private static List<String>  getAllTopicFromGroup(String groupId) throws InterruptedException, ExecutionException, TimeoutException {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_SERVERS);

        AdminClient client = AdminClient.create(props);

        Collection<ConsumerGroupListing> consumerGroupListings = client.listConsumerGroups().valid().get(100, TimeUnit.SECONDS);
        List<String> groups = consumerGroupListings.stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());

        if(!groups.contains(groupId)){
            return new ArrayList<String>() ;
        }

        ListConsumerGroupOffsetsResult offset = client.listConsumerGroupOffsets(groupId);
        KafkaFuture<Map<TopicPartition, OffsetAndMetadata>> mapKafkaFuture = offset.partitionsToOffsetAndMetadata();
        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = mapKafkaFuture.get();
        // 获取keySet
        Set<TopicPartition> topicPartitions = topicPartitionOffsetAndMetadataMap.keySet();
        // 定义一个数值来接受偏移量
        Set<String> topics =  new HashSet<String>();
        for (TopicPartition top : topicPartitions) {
            OffsetAndMetadata offsetAndMetadata = topicPartitionOffsetAndMetadataMap.get(top);

            topics.add(top.topic());
        }
        return new ArrayList<String>(topics);
    }




    /**
     * 获得对应GroupId的 Topic的偏移量
     * @param groupId
     * @param topicName
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    private static Long getGroupsForTopic(String groupId,String topicName) throws InterruptedException, ExecutionException, TimeoutException {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_SERVERS);

        AdminClient client = AdminClient.create(props);

        Collection<ConsumerGroupListing> consumerGroupListings = client.listConsumerGroups().valid().get(100, TimeUnit.SECONDS);
        List<String> groups = consumerGroupListings.stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());

        if(!groups.contains(groupId)){
            return 0L;
        }

        ListConsumerGroupOffsetsResult offset = client.listConsumerGroupOffsets(groupId);
        KafkaFuture<Map<TopicPartition, OffsetAndMetadata>> mapKafkaFuture = offset.partitionsToOffsetAndMetadata();
        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = mapKafkaFuture.get();
        // 获取keySet
        Set<TopicPartition> topicPartitions = topicPartitionOffsetAndMetadataMap.keySet();
        // 定义一个数值来接受偏移量
        Long consumerOffset = 0L;
        for (TopicPartition top : topicPartitions) {
            OffsetAndMetadata offsetAndMetadata = topicPartitionOffsetAndMetadataMap.get(top);
            if(topicName.equals(top.topic())){
                consumerOffset+=offsetAndMetadata.offset();
            }
        }
        return consumerOffset;
    }

    /**
     * 获得所有的消费者组
     * @return
     */
    public static List<String> getAllGroups(){
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_SERVERS);

        AdminClient client = AdminClient.create(props);
        Collection<ConsumerGroupListing> consumerGroupListings = null;
        try {
            consumerGroupListings = client.listConsumerGroups().valid().get(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        List<String> groups = consumerGroupListings.stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());
        return  groups;
    }


    /**
     * 获取对应的  Group   消费Topic的情况
     * @param groupId
     * @param topic
     * @return
     * @throws Exception
     */
    public static Long  getLagByGroupAndTopic(String groupId, String topic) throws Exception {
        Properties consumeProps = getConsumerProperties(groupId);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumeProps);
        // 查询topic partitions
        List<PartitionInfo> partitionsFor = consumer.partitionsFor(topic);
        //由于有时延， 尽量逐个topic查询， 减少lag为负数的情况
        List<TopicPartition> topicPartitions = new ArrayList<>();

        // 获取topic对应的 TopicPartition
        for (PartitionInfo partitionInfo : partitionsFor) {
            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
            topicPartitions.add(topicPartition);
        }
        Long topicTotalOffset = 0L;
        // 查询logSize
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
        for (Map.Entry<TopicPartition, Long> entry : endOffsets.entrySet()) {
            TopicPartition partitionInfo = entry.getKey();
            topicTotalOffset+=endOffsets.get(partitionInfo);
        }
        consumer.close();

        return topicTotalOffset;
    }


    /**
     * 获得所有的Topic
     * @return
     */
    private static List<String>  getAllTopics(){
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_SERVERS);
        AdminClient client = AdminClient.create(props);
        ListTopicsResult listTopicsResult = client.listTopics();
        KafkaFuture<Set<String>> names = listTopicsResult.names();
        Set<String> topics = null;
        try {
            topics = names.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return new ArrayList<String>(topics);
    }

    /**
     * 获取消费者配置
     * @param groupId
     * @return
     */
    public static Properties getConsumerProperties(String groupId) {
        Properties props = new Properties();
        props.put("group.id", groupId);
        props.put("bootstrap.servers", BROKER_SERVERS);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

}


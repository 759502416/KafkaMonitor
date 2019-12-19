package com.xgit.kafka.monitor;

import com.xgit.kafka.KafkaApplication;
import com.xgit.kafka.bean.Brokers;
import com.xgit.kafka.bean.Offsets;
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
    private static String BROKER_SERVERS = "hdp-3.hadoop:6667,hdp-4.hadoop:6667";
    /**
     * 定义配置项
     */
    private static Properties props = new Properties();

    /**
     * 定义 Kafka 客户端
     */
    private static AdminClient client;

    /**
     * 定义Broker组，方便传递给前端的
     */
    public static List<Brokers> brokers = new ArrayList<>();

    /**
     * 初始化 配置项  和 Kafka客户端
     */
    static {
        // 如果启动jar指定BrokerServers,用指定的BrokerServers
        if (!"".equals(KafkaApplication.BROKER_SERVERS)) {
            BROKER_SERVERS = KafkaApplication.BROKER_SERVERS;
        }
        // 填充brokers
        String[] split = BROKER_SERVERS.split(",");
        for (int i = 0; i < split.length; i++) {
            String broker_serer = split[i];
            String[] ipAndPort = broker_serer.split(":");
            String host = ipAndPort[0];
            String port = ipAndPort[1];
            Brokers tempBroker = new Brokers();
            tempBroker.setHost(host);
            tempBroker.setId(i);
            tempBroker.setPort(Integer.valueOf(port));
            brokers.add(tempBroker);
        }
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_SERVERS);
        client = AdminClient.create(props);
        System.err.println("打开了client!");
    }




    /**
     * 此为简易测试
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 接口1 获取所有的Topic
        List<String> allTopics = getAllTopics();
        // 接口2 获得所有的Group
        List<String> allGroups = getAllGroups();

        // 接口3  根据给定的GroupId，类似我下面的这种group，获得所有它的Topic。
        List<String> allTopicFromGroup = getAllTopicFromGroup(allGroups.get(0));
        //Group   先写死测试
        String group = "XGIT_ENV_XM2M_RAW_HDFS";
        //Topic
        String topic = "XGIT_ENV_XM2M_RAWDATA";
        // Debug 看这里的数据结构,测试用
        List<String> oneTopicAllGroup = getOneTopicAllGroup(topic);
        // Debug 看这里的数据结构,测试用
        ArrayList<Offsets> lagByGroupAndTopicWithPartition = getLagByGroupAndTopicWithPartition(group, topic);

        // 这里可以用定时调度器，此处测试工具图方便而已
        while (true) {
            Thread.sleep(3000);
            // 获得当前对饮 Group的Topic的offset
            Long totalTopicOffset = getLagByGroupAndTopic(group, topic);
            // 获得当前对应
            Long consumerOffsetForTopic = getGroupsForTopic(group, topic);
            System.err.println("Group:" + group + ",Topic:" + topic + ",TotalOffset:" + totalTopicOffset + ",NowOffset:" +
                    consumerOffsetForTopic + ",overstockOffset:" + (totalTopicOffset - consumerOffsetForTopic));
        }

    }

    /**
     * 给定一个topic，返回所有消费该Topic的Consumer
     *
     * @param topicName
     * @return
     */
    public static List<String> getOneTopicAllGroup(String topicName) {
        // 新建一个group 的List容器
        List<String> allGroupForOneTopic = new ArrayList<String>();
        // 如果topicName不存在
        if (!getAllTopics().contains(topicName)) {
            allGroupForOneTopic.add("Unable to find Active Consumers");
            return allGroupForOneTopic;
        }
        // 获的所有的group
        List<String> allGroups = getAllGroups();
        allGroups.forEach(groupId -> {
            // 根据groupId，获取对应的Map<TopicPartition, OffsetAndMetadata>
            Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = null;
            try {
                topicPartitionOffsetAndMetadataMap = getTopicOffsetAndMetadata(groupId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 获取keySet
            Set<TopicPartition> topicPartitions = topicPartitionOffsetAndMetadataMap.keySet();
            for (TopicPartition top : topicPartitions) {
                if (topicName.equals(top.topic())) {
                    allGroupForOneTopic.add(groupId);
                    break;
                }
            }
        });
        // 如果不存在有消费者消费该Topic
        if (allGroupForOneTopic.size() < 1) {
            allGroupForOneTopic.add("Unable to find Active Consumers");
        }
        return allGroupForOneTopic;
    }


    /**
     * 根据给定的GprouId,获取它对应的所有Topic
     *
     * @param groupId
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public static List<String> getAllTopicFromGroup(String groupId) throws InterruptedException, ExecutionException, TimeoutException {
        // 根据groupId，获取对应的Map<TopicPartition, OffsetAndMetadata>
        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = getTopicOffsetAndMetadata(groupId);
        // 获取keySet
        Set<TopicPartition> topicPartitions = topicPartitionOffsetAndMetadataMap.keySet();
        // 定义一个数值来接受偏移量
        Set<String> topics = new HashSet<String>();
        for (TopicPartition top : topicPartitions) {
            topics.add(top.topic());
        }
        return new ArrayList<String>(topics);
    }


    /**
     * 获得对应GroupId的 Topic的偏移量
     *
     * @param groupId
     * @param topicName
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    private static Long getGroupsForTopic(String groupId, String topicName) throws InterruptedException, ExecutionException, TimeoutException {
        // 根据groupId，获取对应的Map<TopicPartition, OffsetAndMetadata>
        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = getTopicOffsetAndMetadata(groupId);
        // 获取keySet
        Set<TopicPartition> topicPartitions = topicPartitionOffsetAndMetadataMap.keySet();
        // 定义一个数值来接受偏移量
        Long consumerOffset = 0L;
        for (TopicPartition top : topicPartitions) {
            OffsetAndMetadata offsetAndMetadata = topicPartitionOffsetAndMetadataMap.get(top);
            if (topicName.equals(top.topic())) {
                consumerOffset += offsetAndMetadata.offset();
            }
        }
        return consumerOffset;
    }


    /**
     * 根据groupId,获取对应的TopicPartition  :  offsetAndMetadata
     *
     * @param groupId
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    private static Map<TopicPartition, OffsetAndMetadata> getTopicOffsetAndMetadata(String groupId) throws InterruptedException, ExecutionException, TimeoutException {
        Collection<ConsumerGroupListing> consumerGroupListings = client.listConsumerGroups().valid().get(100, TimeUnit.SECONDS);
        List<String> groups = consumerGroupListings.stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());

        if (!groups.contains(groupId)) {
            return null;
        }
        ListConsumerGroupOffsetsResult offset = client.listConsumerGroupOffsets(groupId);
        KafkaFuture<Map<TopicPartition, OffsetAndMetadata>> mapKafkaFuture = offset.partitionsToOffsetAndMetadata();
        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = mapKafkaFuture.get();
        return topicPartitionOffsetAndMetadataMap;
    }

    /**
     * 获得所有的消费者组
     *
     * @return
     */
    public static List<String> getAllGroups() {
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
        return groups;
    }


    /**
     * 获取对应的  Group   消费Topic的情况
     *
     * @param groupId
     * @param topic
     * @return
     * @throws Exception
     */
    public static Long getLagByGroupAndTopic(String groupId, String topic) throws Exception {
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
            topicTotalOffset += endOffsets.get(partitionInfo);
        }
        consumer.close();

        return topicTotalOffset;
    }


    /**
     * 获得所有的Topic
     *
     * @return
     */
    public static List<String> getAllTopics() {
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
     *
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


    /**
     * 给Topic，给group,返回Offsets
     *
     * @param groupId
     * @param topic
     * @return
     * @throws Exception
     */
    public static ArrayList<Offsets> getLagByGroupAndTopicWithPartition(String groupId, String topic) {
        // 新建一个Offsets 描述组
        ArrayList<Offsets> offsets = new ArrayList<>();
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
        // 查询logSize
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
        // 根据groupId，获取对应的Map<TopicPartition, OffsetAndMetadata>
        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = null;
        try {
            topicPartitionOffsetAndMetadataMap = getTopicOffsetAndMetadata(groupId);
            if (null == topicPartitionOffsetAndMetadataMap){
                // 为空，则是前端给错了参数，直接返回
                return new ArrayList<Offsets>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<TopicPartition, Long> entry : endOffsets.entrySet()) {
            // 新建一个Offset载体
            Offsets partitionOffset = new Offsets();
            TopicPartition partitionInfo = entry.getKey();
            // 获取Partition的编码
            int partition = partitionInfo.partition();
            // 获取Topic的总偏移量
            Long logSize = endOffsets.get(partitionInfo);
            // 获取到偏移量元数据
            OffsetAndMetadata offsetAndMetadata = topicPartitionOffsetAndMetadataMap.get(partitionInfo);
            if (offsetAndMetadata == null) {
                continue;
            }
            partitionOffset.setTopic(topic);
            partitionOffset.setGroup(groupId);
            partitionOffset.setPartition(partition);
            partitionOffset.setLogSize(logSize);
            partitionOffset.setOffset(offsetAndMetadata.offset());
            // 添加到offset组
            offsets.add(partitionOffset);
        }
        consumer.close();
        return offsets;
    }
}


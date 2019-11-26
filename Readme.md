这是kafka的监测工具。适用于0.9以上版本。

可根据接口返回的json，自行设计前端页面对接

2019年11月25日晚上 第一版

工具类在src/main/java/com/xgit/kafka/monitor/KafkaUtil.java 中，里面有初步的讲解
后续会马上完善。

要符合你的业务需求，你只需要修改BrokersServer的地址



1.修改BrokersServer的地址

?	你可以选择两种办法：①去src/main/java/com/xgit/kafka/monitor/KafkaUtil.java 修改

?							②在启动SpringBoot 项目时   启动参数指定



2.接口讲解

 /topicdetails/{topicName}  获取你的Topic对应的消费者组们

![给Topic求对应的所有消费者组](https://github.com/759502416/KafkaMonitor/tree/master/image/给Topic求对应的所有消费者组.png)

   /simple/{topicName}/{groupName}  直接快速获取你要的kafka监测

![直接快速获取的接口](https://github.com/759502416/KafkaMonitor/tree/master/image/直接快速获取的接口.png)

/group/{groupName}  如果groupName 不存在，则会返回空

![给不存在的group](https://github.com/759502416/KafkaMonitor/tree/master/image/给不存在的group.png)

/topicdetails/{topicName}  如果TopicName不存在，或者真的没用消费者消费，会返回Unable to .....

![给错误的Topic求对应的消费者组](https://github.com/759502416/KafkaMonitor/tree/master/image/给错误的Topic求对应的消费者组.png)

/group/{groupName}  返回brokers列表，和对应的消费者信息

![给定grup，获取所消费的topic](https://github.com/759502416/KafkaMonitor/tree/master/image/给定grup，获取所消费的topic.png)



/group     获得所有的group列表

![获取所有的Group](https://github.com/759502416/KafkaMonitor/tree/master/image/获取所有的Group.png)

/topiclist  获得所有的topic 列表

![求所有的Topic](https://github.com/759502416/KafkaMonitor/tree/master/image/求所有的Topic.png)
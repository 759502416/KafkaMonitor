����kafka�ļ�⹤�ߡ�
2019��11��25������ ��һ��

��������src/main/java/com/xgit/kafka/monitor/KafkaUtil.java �У������г����Ľ���
�������������ơ�

Ҫ�������ҵ��������ֻ��Ҫ�޸�BrokersServer�ĵ�ַ



1.�޸�BrokersServer�ĵ�ַ

?	�����ѡ�����ְ취����ȥsrc/main/java/com/xgit/kafka/monitor/KafkaUtil.java �޸�

?							��������SpringBoot ��Ŀʱ   ��������ָ��



2.�ӿڽ���

 /topicdetails/{topicName}  ��ȡ���Topic��Ӧ������������

![��Topic���Ӧ��������������](.\image\��Topic���Ӧ��������������.png)

   /simple/{topicName}/{groupName}  ֱ�ӿ��ٻ�ȡ��Ҫ��kafka���

![ֱ�ӿ��ٻ�ȡ�Ľӿ�](.\image\ֱ�ӿ��ٻ�ȡ�Ľӿ�.png)

/group/{groupName}  ���groupName �����ڣ���᷵�ؿ�

![�������ڵ�group](.\image\�������ڵ�group.png)

/topicdetails/{topicName}  ���TopicName�����ڣ��������û�����������ѣ��᷵��Unable to .....

![�������Topic���Ӧ����������](.\image\�������Topic���Ӧ����������.png)

/group/{groupName}  ����brokers�б��Ͷ�Ӧ����������Ϣ

![����grup����ȡ�����ѵ�topic](.\image\����grup����ȡ�����ѵ�topic.png)



/group     ������е�group�б�

![��ȡ���е�Group](.\image\��ȡ���е�Group.png)

/topiclist  ������е�topic �б�

![�����е�Topic](.\image\�����е�Topic.png)
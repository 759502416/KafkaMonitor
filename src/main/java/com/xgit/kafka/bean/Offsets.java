package com.xgit.kafka.bean;

/**
 * @program: KafkaMonitoring
 * @description: Offset描述
 * @author: Mr.Wang
 * @create: 2019-11-26 10:15
 **/
public class Offsets {

    /**
     * 组名
     */
    private String group;
    /**
     * Topic name
     */
    private String topic;
    /**
     * 分区信息
     */
    private int partition;
    /**
     * 分区的offset
     */
    private long offset;
    /**
     * 分区总Size
     */
    private long logSize;
    /**
     * 拥有者
     */
    private String Owner;
    private long creation;
    private long modified;

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

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLogSize() {
        return logSize;
    }

    public void setLogSize(long logSize) {
        this.logSize = logSize;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public long getCreation() {
        return creation;
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }
}

package cn.vpclub.spring.boot.kafka.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Spring Boot Kafka.
 *
 * @author John Deng
 * 2016-08-08
 */
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    public KafkaProperties() {
        this.topics = new Topics();
    }

    private Topics topics;

    private String group;

    private String key;

    private String broker;

    private String zookeeper;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public Topics getTopics() {
        return topics;
    }

    public void setTopics(Topics topics) {
        this.topics = topics;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @ConfigurationProperties(prefix = "topics")
    public class Topics {

        private String producer;

        private String consumer;

        public String getProducer() {
            return producer;
        }

        public void setProducer(String producer) {
            this.producer = producer;
        }

        public String getConsumer() {
            return consumer;
        }

        public void setConsumer(String consumer) {
            this.consumer = consumer;
        }
    };

}

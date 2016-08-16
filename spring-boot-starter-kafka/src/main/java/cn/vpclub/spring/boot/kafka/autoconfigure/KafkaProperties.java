package cn.vpclub.spring.boot.kafka.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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

    private long timeout;

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

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @ConfigurationProperties(prefix = "topics")
    public class Topics {

        private List<String> producers;

        private List<String> consumers;


        public List<String> getProducers() {
            return producers;
        }

        public void setProducers(List<String> producers) {
            this.producers = producers;
        }

        public List<String> getConsumers() {
            return consumers;
        }

        public void setConsumers(List<String> consumers) {
            this.consumers = consumers;
        }
    };

}

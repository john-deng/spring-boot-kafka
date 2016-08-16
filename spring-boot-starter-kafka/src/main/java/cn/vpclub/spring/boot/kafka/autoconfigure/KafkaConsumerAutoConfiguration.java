package cn.vpclub.spring.boot.kafka.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.kafka.serializer.common.StringDecoder;
import org.springframework.integration.kafka.support.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by johnd on 8/9/16.
 */

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConsumerAutoConfiguration {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    Map<String, ConsumerConfiguration> consumerConfigurations() {
        Map<String, ConsumerConfiguration> map = new HashMap<>();

        for (String topic: this.kafkaProperties.getTopics().getConsumers()) {

            TopicCreator topicCreator = new TopicCreator(topic, this.kafkaProperties.getZookeeper());

            Properties properties = new Properties();
            properties.put("zookeeper.connect", this.kafkaProperties.getZookeeper());
            properties.put("group.id", "foo");

            ConsumerConnectionProvider consumerConnectionProvider = new ConsumerConnectionProvider(new kafka.consumer.ConsumerConfig(properties));

            MessageLeftOverTracker messageLeftOverTracker = new MessageLeftOverTracker();

            Map<String, Integer> topicStreamMap = new HashMap<>();
            topicStreamMap.put(topic, 1);

            ConsumerMetadata consumerMetadata = new ConsumerMetadata();
            consumerMetadata.setGroupId("group.foo");
            consumerMetadata.setValueDecoder(new StringDecoder());
            consumerMetadata.setKeyDecoder(new StringDecoder());
            consumerMetadata.setTopicStreamMap(topicStreamMap);

            ConsumerConfiguration consumerConfiguration = new ConsumerConfiguration(
                    consumerMetadata,
                    consumerConnectionProvider,
                    messageLeftOverTracker);

            map.put(topic, consumerConfiguration);
        }

        return map;
    }

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    KafkaConsumerContext consumerContext() {
        return new KafkaConsumerContext();
    }

}

package cn.vpclub.spring.boot.kafka.autoconfigure;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.messaging.PollableChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by johnd on 8/9/16.
 */

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConsumerAutoConfiguration {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public KafkaMessageListenerContainer<String, String> container() throws Exception {
        return new KafkaMessageListenerContainer<>(
                consumerFactory(),
                new ContainerProperties(new TopicPartitionInitialOffset(this.kafkaProperties.getTopics().getConsumer(), 0)));
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaProperties.getBroker());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.kafkaProperties.getGroup());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public KafkaMessageDrivenChannelAdapter<String, String> adapter(KafkaMessageListenerContainer<String, String> container) {
        KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter =
                new KafkaMessageDrivenChannelAdapter<String, String>(container);
        kafkaMessageDrivenChannelAdapter.setOutputChannel(fromKafka());
        return kafkaMessageDrivenChannelAdapter;
    }

    @Bean
    public PollableChannel fromKafka() {
        return new QueueChannel();
    }

    @Bean
    public TopicCreator topicCreator() {
        return new TopicCreator(this.kafkaProperties.getTopics().getConsumer(), this.kafkaProperties.getZookeeper());
    }
}

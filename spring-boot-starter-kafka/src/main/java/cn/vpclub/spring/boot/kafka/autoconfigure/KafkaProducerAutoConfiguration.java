package cn.vpclub.spring.boot.kafka.autoconfigure;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaProducerAutoConfiguration
 * Created by johnd on 8/9/16.
 */

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaProducerAutoConfiguration {

//    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KafkaProperties kafkaProperties;

    @ServiceActivator(inputChannel = "toKafka")
    @Bean
    public MessageHandler handler() throws Exception {
        KafkaProducerMessageHandler<String, String> handler =
                new KafkaProducerMessageHandler<String, String>(kafkaTemplate());
        handler.setTopicExpression(new LiteralExpression(this.kafkaProperties.getTopics().getProducer()));
        handler.setMessageKeyExpression(new LiteralExpression(this.kafkaProperties.getKey()));
        return handler;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaProperties.getBroker());
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public TopicCreator topicCreator() {
        return new TopicCreator(this.kafkaProperties.getTopics().getProducer(), this.kafkaProperties.getZookeeper());
    }
}

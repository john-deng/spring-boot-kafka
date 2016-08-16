package cn.vpclub.spring.boot.kafka.autoconfigure;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.kafka.core.BrokerAddress;
import org.springframework.integration.kafka.core.BrokerAddressListConfiguration;
import org.springframework.integration.kafka.core.ConnectionFactory;
import org.springframework.integration.kafka.core.DefaultConnectionFactory;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.integration.kafka.support.KafkaProducerContext;
import org.springframework.integration.kafka.support.ProducerConfiguration;
import org.springframework.integration.kafka.support.ProducerFactoryBean;
import org.springframework.integration.kafka.support.ProducerMetadata;
import org.springframework.messaging.MessageHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

    @Bean
    public KafkaProducerContext producerContext() throws Exception {

        KafkaProducerContext kafkaProducerContext = new KafkaProducerContext();
        Map<String, ProducerConfiguration<?, ?>> producerConfigurationMap = new HashMap<>();

        for (String topic: this.kafkaProperties.getTopics().getProducers()) {
            TopicCreator topicCreator = new TopicCreator(topic, this.kafkaProperties.getZookeeper());

            ProducerMetadata<String, String> producerMetadata = new ProducerMetadata<>(
                    topic,
                    String.class,
                    String.class,
                    new StringSerializer(),
                    new StringSerializer());
            Properties props = new Properties();
            props.put("linger.ms", "1000");
            ProducerFactoryBean<String, String> producer = new ProducerFactoryBean<>(producerMetadata, this.kafkaProperties.getBroker(), props);
            ProducerConfiguration<String, String> config = new ProducerConfiguration<>(producerMetadata, producer.getObject());
            producerConfigurationMap.put(topic, config) ;
        }
        kafkaProducerContext.setProducerConfigurations(producerConfigurationMap);
        return kafkaProducerContext;
    }
}

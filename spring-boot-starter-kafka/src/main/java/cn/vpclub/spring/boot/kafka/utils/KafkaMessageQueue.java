package cn.vpclub.spring.boot.kafka.utils;

import cn.vpclub.spring.boot.kafka.autoconfigure.KafkaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.kafka.support.ConsumerConfiguration;
import org.springframework.integration.kafka.support.KafkaConsumerContext;
import org.springframework.integration.kafka.support.KafkaProducerContext;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * KafkaMessageHandler
 * Created by johnd on 8/10/16.
 */
@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class KafkaMessageQueue implements ApplicationContextAware {
    static Logger logger = LoggerFactory.getLogger(KafkaMessageQueue.class);

    @Autowired
    private KafkaProperties kafkaProperties;

    private ApplicationContext appContext;

    private KafkaProducerContext producerContext;

    private KafkaConsumerContext consumerContext;

    Map<String, ConsumerConfiguration> consumerConfigurations;

    private ExecutorService executorService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }

    public KafkaMessageQueue(ApplicationContext appContext) {
        this.appContext = appContext;
        this.producerContext = appContext.getBean("producerContext", KafkaProducerContext.class);
        this.consumerContext = appContext.getBean("consumerContext", KafkaConsumerContext.class);
        this.consumerConfigurations = appContext.getBean("consumerConfigurations", HashMap.class);
        this.executorService = Executors.newCachedThreadPool();
    }

    public String send(String producerTopic, String consumerTopic, String key, String payload, long timeout) {

        producerContext.send(producerTopic, key, payload);

        if (null != consumerTopic) {
            return receive(consumerTopic, timeout);
        }

        return null;
    }

    public String send(String producerTopic, String consumerTopic, String key, String payload) {
        return send(producerTopic, consumerTopic, key, payload, 2000);
    }

    public String send(String producerTopic, String key, String payload) {
        return send(producerTopic, null, key, payload, 2000);
    }

    public String receive(String topic) {

        Message<?> r = null;

        Map<String, ConsumerConfiguration> cfg = new HashMap<>();
        cfg.clear();
        cfg.put(topic, consumerConfigurations.get(topic));
        consumerContext.setConsumerConfigurations(cfg);

        while (null == r) {
            r = consumerContext.receive();
        }

        return r.getPayload().toString();
    }

    public String receive(String topic, long timeout) {
        List<String> results = new ArrayList<>();
        results.clear();
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {

            String r = receive(topic);

            if (null != r) {
                results.add(r);
            }

            System.out.println("async thread: " +  topic);
        }, executorService);

        boolean hasTimeout = (timeout > 0);
        int retried = 0;
        timeout /= 10;
        while (!completableFuture.isDone() && (!hasTimeout || retried < timeout)) {
            retried++;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }

        if (0 != results.size())
        {
            return results.get(0);
        }

        return null;
    }
}

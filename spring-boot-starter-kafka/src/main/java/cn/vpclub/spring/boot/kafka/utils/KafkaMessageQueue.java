package cn.vpclub.spring.boot.kafka.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

/**
 * KafkaMessageHandler
 * Created by johnd on 8/10/16.
 */
@Component
public class KafkaMessageQueue {

    private PollableChannel receiver;

    private MessageChannel sender;

    private ApplicationContext appContext;

    public KafkaMessageQueue(ApplicationContext appContext) {
        this.appContext = appContext;
        this.sender = appContext.getBean("toKafka", MessageChannel.class);
        this.receiver = appContext.getBean("fromKafka", PollableChannel.class);
    }

    public String send(String message, long timeout) {
        String response = null;

        sender.send(new GenericMessage<String>(message));
        if (0 != timeout) {
            response = receive(timeout);
        }

        return response;
    }

    public String send(String message) {
        return send(message, 0);
    }

    public String send(String topic, String payload, long timeout) {
        String response = null;
        Message<?> message = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        sender.send(message);
        if (0 != timeout) {
            response = receive(timeout);
        }

        return response;
    }

    public String receive(long timeout) {

        Message<?> received = receiver.receive(timeout);
        if (null != received) {
            String response = (String)received.getPayload();
            return response;
        }

        return null;
    }

}

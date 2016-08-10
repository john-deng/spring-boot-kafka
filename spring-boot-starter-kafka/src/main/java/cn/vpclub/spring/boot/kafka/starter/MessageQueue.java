package cn.vpclub.spring.boot.kafka.starter;

import org.springframework.context.ApplicationContext;
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
public class MessageQueue {

    private PollableChannel receiver;

    private MessageChannel sender;

    private ApplicationContext appContext;

    public MessageQueue(ApplicationContext appContext) {
        this.appContext = appContext;
        this.sender = appContext.getBean("toKafka", MessageChannel.class);
        this.receiver = appContext.getBean("fromKafka", PollableChannel.class);
    }

    public String send(String message, long timeout) {
        String response = null;

        sender.send(new GenericMessage<String>(message));
//        System.out.println("sent: " + message);
        if (0 != timeout) {
            response = receive(timeout);
//            if (null != response) {
//                System.out.println("response: " + response);
//            }
        }

        return response;
    }

    public String send(String message) {
        return send(message, 0);
    }

    public String receive(long timeout) {

        Message<?> received = receiver.receive(timeout);
        if (null != received) {
            String response = (String)received.getPayload();
//            System.out.println("received: " + response);
            return response;
        }

        return null;
    }

}

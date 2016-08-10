/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.vpclub.spring.boot.kafka.demo1;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

import static org.apache.kafka.common.utils.Utils.sleep;

/**
 * @author John Deng
 * @since 4.2
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context
                = new SpringApplicationBuilder(Application.class)
                .web(false)
                .run(args);
        MessageChannel toKafka = context.getBean("toKafka", MessageChannel.class);
        PollableChannel receiver = context.getBean("fromKafka", PollableChannel.class);
        for (int i = 0; i < 10000; i++) {
            String message = "the message from demo1 to demo2, count: " + i;
            toKafka.send(new GenericMessage<String>(message));
            System.out.println("sent: " + message);
            sleep(500L);

            Message<?> received = receiver.receive(100);
            if (null != received) {
                System.out.println("received reply from demo2: " + received);
                sleep(500L);
            }
        }
        context.close();
        System.exit(0);
    }
}

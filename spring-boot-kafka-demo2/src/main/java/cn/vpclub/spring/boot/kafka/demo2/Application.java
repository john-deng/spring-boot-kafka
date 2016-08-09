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

package cn.vpclub.spring.boot.kafka.demo2;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * @author Gary Russell
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
        Message<?> received = receiver.receive(10000);
        for (int i = 0; i < 100000; i++) {
            if (null != received) {
                System.out.println("received: " + received);

                // send reply to demo1
                toKafka.send(new GenericMessage<String>("replied from demo2: " + received.getPayload().toString()));
            }

            received = receiver.receive(100);
        }

        context.close();
        System.exit(0);
    }
}

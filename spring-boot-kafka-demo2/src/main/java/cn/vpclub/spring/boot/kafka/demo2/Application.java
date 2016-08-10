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

import cn.vpclub.spring.boot.kafka.starter.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.apache.kafka.common.utils.Utils.sleep;

/**
 * @author John Deng
 * @since 4.2
 */
@SpringBootApplication
public class Application {

    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context
                = new SpringApplicationBuilder(Application.class)
                .web(false)
                .run(args);

        MessageQueue kafkaMessageHandler = new MessageQueue(context);
        for (int i = 0; i < 10000; i++) {
            String response = kafkaMessageHandler.receive(1000);
            kafkaMessageHandler.send("replied: " + response);

            logger.info("received: " + response);
            logger.info("replied: " + response);

            sleep(100L);
        }

        context.close();
        System.exit(0);
    }
}

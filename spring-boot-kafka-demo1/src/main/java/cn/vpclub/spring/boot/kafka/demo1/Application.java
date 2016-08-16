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

import cn.vpclub.spring.boot.kafka.utils.KafkaMessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static java.lang.Thread.sleep;


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

        KafkaMessageQueue mq = new KafkaMessageQueue(context);

        String message = null;
        String response = null;
        for (int i = 0; i < 10; i++) {
            message = "the message a from demo1 to demo2, count: " + i;
            response = mq.send("demo1.to.demo2", "demo2.to.demo1", "test.key", message, 0);
            logger.info(message);
            if (null != response) {
                logger.info("received from demo2: " + response);
            }
            sleep(10L);

            message = "the message b from demo1 to demo3, count: " + i;
            response = mq.send("demo1.to.demo3", "demo3.to.demo1", "test.key", message, 0);
            logger.info(message);
            if (null != response) {
                logger.info("received from demo3: " + response);
            }
            sleep(10L);
        }

        context.close();
        System.exit(0);
    }
}

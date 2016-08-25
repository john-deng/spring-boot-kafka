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
        for (int i = 0; i < 10; i++) {
            String response = mq.receive("demo1.to.demo2", 0);
            if (null != response) {
                mq.send("demo2.to.demo1", null, "test.key", "message replied from demo2", 0);
            }

            logger.info("received: " + response);
            logger.info("sent back to demo1: " + response);

            sleep(100L);
        }

        context.close();
        System.exit(0);
    }
}

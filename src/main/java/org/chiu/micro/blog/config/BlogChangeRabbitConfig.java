package org.chiu.micro.blog.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mingchiuli
 * @create 2022-12-25 4:13 pm
 */
@Configuration
public class BlogChangeRabbitConfig {

    public static final String ES_QUEUE = "blog.change.queue.es";

    public static final String CACHE_QUEUE = "blog.change.queue.cache";

    public static final String TOPIC_EXCHANGE = "blog.change.topic.exchange";

    public static final String ES_BINDING_KEY = "blog.change.binding.es";

    public static final String CACHE_BINDING_KEY = "blog.change.binding.cache";

    @Bean("esQueue")
    Queue esQueue() {
        return new Queue(ES_QUEUE, true, false, false);
    }

    @Bean("cacheQueue")
    Queue cahceQueue() {
        return new Queue(CACHE_QUEUE, true, false, false);
    }

    //ES交换机
    @Bean("topicExchange")
    TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    //绑定ES队列和ES交换机
    @Bean("esBinding")
    Binding esBinding(@Qualifier("esQueue") Queue esQueue,
                      @Qualifier("topicExchange") TopicExchange esExchange) {
        return BindingBuilder
                .bind(esQueue)
                .to(esExchange)
                .with(ES_BINDING_KEY);
    }

    @Bean("cacheBinding")
    Binding cacheBinding(@Qualifier("cacheQueue") Queue cacheQueue,
                         @Qualifier("topicExchange") TopicExchange esExchange) {
        return BindingBuilder
                .bind(cacheQueue)
                .to(esExchange)
                .with(ES_BINDING_KEY);
    }
}

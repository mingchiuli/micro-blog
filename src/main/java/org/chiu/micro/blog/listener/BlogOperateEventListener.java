package org.chiu.micro.blog.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chiu.micro.blog.event.BlogOperateEvent;
import org.chiu.micro.blog.lang.Const;
import org.chiu.micro.blog.constant.BlogOperateEnum;
import org.chiu.micro.blog.constant.BlogOperateMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlogOperateEventListener {

    private final RabbitTemplate rabbitTemplate;

    private final StringRedisTemplate redisTemplate;

    private static final String ES_EXCHANGE = "es.direct.exchange";

    private static final String ES_BINDING_KEY = "es.binding";

    private static final String CACHE_BLOG_EVICT_EXCHANGE = "cache.blog.direct.exchange";

    private static final String CACHE_BLOG_EVICT_BINDING_KEY = "cache.blog.evict.binding";

    @EventListener
    @Async("commonExecutor")
    public void process(BlogOperateEvent event) {
        BlogOperateMessage messageBody = event.getBlogOperateMessage();
        BlogOperateEnum typeEnum = messageBody.getTypeEnum();
        String name = typeEnum.name();
        Long blogId = messageBody.getBlogId();
        String key = name + "_" + blogId;

        var correlationData = new CorrelationData();
        redisTemplate.opsForValue().set(Const.CONSUME_MONITOR.getInfo() + correlationData.getId(),
                key,
                30,
                TimeUnit.MINUTES);
        rabbitTemplate.convertAndSend(ES_EXCHANGE,
                ES_BINDING_KEY,
                messageBody,
                correlationData);

        rabbitTemplate.convertAndSend(CACHE_BLOG_EVICT_EXCHANGE,
                CACHE_BLOG_EVICT_BINDING_KEY,
                messageBody);
    }
}

package org.chiu.micro.blog.listener;

import lombok.RequiredArgsConstructor;
import org.chiu.micro.blog.event.BlogOperateEvent;
import org.chiu.micro.blog.constant.BlogOperateEnum;
import org.chiu.micro.blog.constant.BlogOperateMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BlogOperateEventListener {

    private final RabbitTemplate rabbitTemplate;

    private static final String ES_EXCHANGE = "es.direct.exchange";

    private static final String ES_BINDING_KEY = "es.binding";

    private static final String CACHE_BLOG_EVICT_EXCHANGE = "cache.blog.direct.exchange";

    private static final String CACHE_BLOG_EVICT_BINDING_KEY = "cache.blog.evict.binding";

    @EventListener
    @Async("commonExecutor")
    public void process(BlogOperateEvent event) {
        BlogOperateMessage messageBody = event.getBlogOperateMessage();

        rabbitTemplate.convertAndSend(ES_EXCHANGE,
                ES_BINDING_KEY,
                messageBody);

        rabbitTemplate.convertAndSend(CACHE_BLOG_EVICT_EXCHANGE,
                CACHE_BLOG_EVICT_BINDING_KEY,
                messageBody);
    }
}

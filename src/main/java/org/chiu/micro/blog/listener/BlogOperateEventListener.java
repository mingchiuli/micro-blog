package org.chiu.micro.blog.listener;

import lombok.RequiredArgsConstructor;
import org.chiu.micro.blog.event.BlogOperateEvent;
import org.chiu.micro.blog.config.BlogChangeRabbitConfig;
import org.chiu.micro.blog.constant.BlogOperateMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BlogOperateEventListener {

    private final RabbitTemplate rabbitTemplate;

    private static final String BINDING_KEY_MODE = "blog.change.binding.#";

    @EventListener
    @Async("commonExecutor")
    public void process(BlogOperateEvent event) {
        BlogOperateMessage messageBody = event.getBlogOperateMessage();

        rabbitTemplate.convertAndSend(BlogChangeRabbitConfig.TOPIC_EXCHANGE,
                BINDING_KEY_MODE,
                messageBody);
    }
}

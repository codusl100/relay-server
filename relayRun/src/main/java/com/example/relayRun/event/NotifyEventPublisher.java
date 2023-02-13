package com.example.relayRun.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotifyEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishNotifyEvent(Object event) {
        applicationEventPublisher.publishEvent(event);
    }
}

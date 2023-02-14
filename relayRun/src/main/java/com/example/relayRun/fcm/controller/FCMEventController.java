package com.example.relayRun.fcm.controller;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.event.BatonTouchEvent;
import com.example.relayRun.event.TimeToRunEvent;
import com.example.relayRun.fcm.service.FCMService;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class FCMEventController {
    private FCMService fcmService;

    public FCMEventController(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @Async
    @EventListener
    public void notifyTimeToRun(TimeToRunEvent event) {
        log.info("time to run !");
        fcmService.sendTimeToRunMessage(event.getMemberStatusIdx(), event.getStart());
    }

    @Async
    @EventListener
    public void notifyBatonTouch(BatonTouchEvent event){
        log.info("baton touch!");
        try {
            fcmService.sendBatonTouchMessage(
                    event.getFromUserProfile(),
                    event.getDay(),
                    event.getEndTime()
            );
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
        }
    }
}

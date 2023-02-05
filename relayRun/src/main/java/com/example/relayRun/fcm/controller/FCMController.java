package com.example.relayRun.fcm.controller;

import com.example.relayRun.fcm.dto.PostDeviceReq;
import com.example.relayRun.fcm.dto.PostDeviceRes;
import com.example.relayRun.fcm.service.FCMService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/device")
public class FCMController {
    FCMService fcmService;

    public FCMController(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/")
    public BaseResponse<PostDeviceRes> saveToken(@RequestBody PostDeviceReq req) {
        try{
            PostDeviceRes result = fcmService.saveDeviceToken(req);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}

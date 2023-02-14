package com.example.relayRun.fcm.controller;

import com.example.relayRun.fcm.dto.PostDeviceReq;
import com.example.relayRun.fcm.dto.PostDeviceRes;
import com.example.relayRun.fcm.service.FCMService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users/device")
public class FCMController {
    FCMService fcmService;

    public FCMController(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/")
    public BaseResponse<PostDeviceRes> saveToken(Principal principal, @RequestBody PostDeviceReq req) {
        try{
            PostDeviceRes result = fcmService.saveDeviceToken(principal, req);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @DeleteMapping("/")
    public BaseResponse<String> deleteToken(Principal principal, @RequestBody PostDeviceReq req) {
        try{
            fcmService.deleteDeviceToken(req);
            return new BaseResponse<>("삭제 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}

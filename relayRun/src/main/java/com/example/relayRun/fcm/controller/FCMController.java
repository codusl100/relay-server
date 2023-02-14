package com.example.relayRun.fcm.controller;

import com.example.relayRun.fcm.dto.PostDeviceReq;
import com.example.relayRun.fcm.dto.PostDeviceRes;
import com.example.relayRun.fcm.service.FCMService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users/device")
public class FCMController {
    FCMService fcmService;

    public FCMController(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @ApiOperation(value = "로그인 성공시 호출, FCM 토큰 서버에 저장", notes = "로그인 인증 정보 필요, 생성된 디바이스 토큰 서버에 저장")
    @PostMapping("/")
    public BaseResponse<PostDeviceRes> saveToken(Principal principal, @ApiParam(value = "알람을 전송 받을 디바이스 토큰") @RequestBody PostDeviceReq req) {
        try{
            PostDeviceRes result = fcmService.saveDeviceToken(principal, req);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value = "로그아웃 시 호출, FCM 토큰 서버에서 삭제")
    @PostMapping("/delete")
    public BaseResponse<String> deleteToken(Principal principal,
                                            @ApiParam(value = "삭제할 디바이스 토큰") @RequestBody PostDeviceReq req) {
        try{
            fcmService.deleteDeviceToken(principal, req);
            return new BaseResponse<>("삭제 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}

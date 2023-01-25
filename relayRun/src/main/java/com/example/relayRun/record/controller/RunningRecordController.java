package com.example.relayRun.record.controller;

import com.example.relayRun.record.dto.PostRunningFinishReq;
import com.example.relayRun.record.dto.PostRunningFinishRes;
import com.example.relayRun.record.dto.PostRunningInitReq;
import com.example.relayRun.record.dto.PostRunningInitRes;
import com.example.relayRun.record.service.RunningRecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/record")
public class RunningRecordController {
    RunningRecordService runningRecordService;

    @Autowired
    public RunningRecordController(RunningRecordService runningRecordService){
        this.runningRecordService = runningRecordService;
    }
    @PostMapping("/start")
    @ApiOperation(value="달리기 시작", notes="profileIdx, 속한 clubIdx 요청")
    public BaseResponse<PostRunningInitRes> startRunning(@RequestBody PostRunningInitReq runningInitReq) {
        try{
            PostRunningInitRes result = runningRecordService.startRunning(runningInitReq);
            return new BaseResponse<>(result);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/finish")
    public BaseResponse<PostRunningFinishRes> finishRunning(@RequestBody PostRunningFinishReq runningFinishReq) {
        try{
            PostRunningFinishRes result = runningRecordService.finishRunning(runningFinishReq);
            return new BaseResponse<>(result);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}

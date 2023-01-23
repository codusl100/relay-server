package com.example.relayRun.record.controller;

import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.service.RecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/record")
public class RecordController {

    private RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @ApiOperation(value="기록 idx로 조회", notes="path variable에 조회할 기록의 idx를 입력해주세요")
    @ResponseBody
    @GetMapping("/{idx}")
    public BaseResponse<Optional<RunningRecordEntity>> getRecordByIdx(@PathVariable("idx") Long idx) {
        try {
            Optional<RunningRecordEntity> rec = recordService.getRecordByIdx(idx);
            return new BaseResponse<>(rec);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

}
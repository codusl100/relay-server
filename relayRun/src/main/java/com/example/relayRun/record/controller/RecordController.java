package com.example.relayRun.record.controller;

import com.example.relayRun.record.dto.GetRecordByIdxRes;
import com.example.relayRun.record.dto.GetRecordWithoutLocationRes;
import com.example.relayRun.record.service.RecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    public BaseResponse<GetRecordByIdxRes> getRecordByIdx(@PathVariable("idx") Long idx) {
        try {
            GetRecordByIdxRes rec = recordService.getRecordByIdx(idx);
            return new BaseResponse<>(rec);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    // member_status_idx와 오늘 날짜로 조회 테스트 API
    @ApiOperation(value="member_status_idx와 오늘 날짜로 조회", notes="Request Parameter : mid, date로 각 값을 입력해주세요")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetRecordWithoutLocationRes>> getRecordWithoutLocation(@RequestParam("mid") Long memberStatusIdx, @RequestParam("date") String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(date + " 00:00:00", formatter);
            LocalDateTime endDate = LocalDateTime.parse(date + " 23:59:59", formatter);
            List<GetRecordWithoutLocationRes> rec = recordService.getRecordWithoutLocation(memberStatusIdx, startDate, endDate);
            return new BaseResponse<>(rec);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

}
package com.example.relayRun.record.service;

import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.repository.RecordRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecordService {

    private RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public Optional<RunningRecordEntity> getRecordByIdx(Long idx) throws BaseException {
        try {
            return recordRepository.findById(idx);
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}

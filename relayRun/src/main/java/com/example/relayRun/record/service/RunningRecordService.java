package com.example.relayRun.record.service;

import com.example.relayRun.record.repository.RunningRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RunningRecordService {
    RunningRecordRepository runningRecordRepository;
    @Autowired
    public RunningRecordService(RunningRecordRepository runningRecordRepository) {
        this.runningRecordRepository = runningRecordRepository;
    }


}

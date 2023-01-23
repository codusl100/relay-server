package com.example.relayRun.record.service;

import com.example.relayRun.record.dto.GetLocationRes;
import com.example.relayRun.record.dto.GetRecordByIdxRes;
import com.example.relayRun.record.entity.LocationEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.repository.LocationRepository;
import com.example.relayRun.record.repository.RecordRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecordService {

    private RecordRepository recordRepository;
    private LocationRepository locationRepository;

    public RecordService(RecordRepository recordRepository, LocationRepository locationRepository) {
        this.recordRepository = recordRepository;
        this.locationRepository = locationRepository;
    }

    public GetRecordByIdxRes getRecordByIdx(Long idx) throws BaseException {
        try {
            Optional<RunningRecordEntity> record = recordRepository.findByRunningRecordIdxAndStatus(idx, "active");
            if (record.isEmpty()) {
                throw new Exception("RECORD_UNAVAILABLE");
            }

            List<GetLocationRes> locationList = locationRepository.findByRecordIdx_RunningRecordIdx(idx);
            return GetRecordByIdxRes.builder()
                    .recordIdx(idx)
                    .date(record.get().getCreatedAt())
                    .time(record.get().getTime())
                    .distance(record.get().getDistance())
                    .pace(record.get().getPace())
                    .goalStatus(record.get().getGoalStatus())
                    .locationList(locationList)
                    .build();

        } catch (Exception e) {
            if (e.getMessage().equals("RECORD_UNAVAILABLE")) {
                throw new BaseException(BaseResponseStatus.RECORD_UNAVAILABLE);
            }
            else {
                System.out.println("e = " + e);
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
        }
    }
}

package com.example.relayRun.record.service;

import com.example.relayRun.record.dto.GetLocationRes;
import com.example.relayRun.record.dto.GetRecordByIdxRes;
import com.example.relayRun.record.dto.GetRecordWithoutLocationRes;
import com.example.relayRun.record.entity.LocationEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.repository.RecordRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class RecordService {

    private RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public GetRecordByIdxRes getRecordByIdx(Long idx) throws BaseException {
        try {
            Optional<RunningRecordEntity> record = recordRepository.findByRunningRecordIdxAndStatus(idx, "active");
            if (record.isEmpty()) {
                throw new Exception("RECORD_UNAVAILABLE");
            }

//            List<GetLocationRes> locationList = locationRepository.findByRecordIdx_RunningRecordIdx(idx);
            List<LocationEntity> getLocations = record.get().getLocations();

            List<GetLocationRes> locationList = new ArrayList<>();
            for (LocationEntity location : getLocations) {
                locationList.add(
                    GetLocationRes.builder()
                        .time(location.getTime())
                        .longitude((float) location.getPosition().getX())
                        .latitude((float) location.getPosition().getY())
                        .status(location.getStatus())
                        .build()
                );
            }

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

    public GetRecordWithoutLocationRes getRecordWithoutLocation(Long memberStatusIdx, LocalDateTime createdAt) throws BaseException {
        try {
            Optional<RunningRecordEntity> optional = recordRepository.findByMemberStatusIdx_MemberStatusIdxAndCreatedAt(memberStatusIdx, createdAt);
            if (optional.isEmpty()) {
                return null;
            }
            RunningRecordEntity record = optional.get();
            return GetRecordWithoutLocationRes.builder()
                    .recordIdx(record.getRunningRecordIdx())
                    .date(record.getCreatedAt())
                    .runningStatus(record.getRunningStatus())
                    .build();
        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}

package com.example.relayRun.record.service;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.record.dto.*;
import com.example.relayRun.record.entity.LocationEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.repository.RunningRecordRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RunningRecordService {
    RunningRecordRepository runningRecordRepository;
    MemberStatusRepository memberStatusRepository;
    UserRepository userRepository;

    @Autowired
    public RunningRecordService(RunningRecordRepository runningRecordRepository,
                                MemberStatusRepository memberStatusRepository,
                                UserRepository userRepository) {
        this.runningRecordRepository = runningRecordRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.userRepository = userRepository;
    }

    /**
     * 달리기 시작 POST
     * @param runningInitReq
     * @return
     * @throws BaseException
     */
    public PostRunningInitRes startRunning(PostRunningInitReq runningInitReq) throws BaseException {
        Optional<MemberStatusEntity> optionalMemberStatus = memberStatusRepository.findByUserProfileIdx_UserProfileIdxAndApplyStatusIs(
                runningInitReq.getProfileIdx(),
                "ACCEPTED"
        );
        if (optionalMemberStatus.isEmpty()){
            throw new BaseException(BaseResponseStatus.POST_RECORD_INVALID_CLUB_ACCESS);
        }
        MemberStatusEntity memberStatus = optionalMemberStatus.get();
        RunningRecordEntity recordEntity = RunningRecordEntity.builder()
                .memberStatusIdx(memberStatus)
                .distance(0.0f).time(0.0f).pace(0.0f)
                .build();
        recordEntity = runningRecordRepository.save(recordEntity);
        PostRunningInitRes result = new PostRunningInitRes();
        result.setRunningRecordIdx(recordEntity.getRunningRecordIdx());
        return result;
    }


    /**
     * 기록 세부 조회 GET
     * @param idx
     * @return
     * @throws BaseException
     */
    public GetRecordByIdxRes getRecordByIdx(Long idx) throws BaseException {
        try {
            Optional<RunningRecordEntity> record = runningRecordRepository.findByRunningRecordIdxAndStatus(idx, "active");
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
            } else {
                System.out.println("e = " + e);
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
        }
    }

    public GetDailyRes getDailyRecord(Principal principal, String date) throws BaseException {
        try {
            Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
            if (user.isEmpty()) {
                throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
            }
            
            LocalDate localDate = LocalDate.parse(date);


        } catch (NullPointerException e) { // principal이 없을 때
            throw new BaseException(BaseResponseStatus.EMPTY_TOKEN);

        } catch (DateTimeParseException e) { // 날짜 형식이 잘못됐을 때
            throw new BaseException(BaseResponseStatus.INVALID_DATE_FORMAT);

        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}

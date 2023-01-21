package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.dto.TimeTableDTO;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MemberStatusService {

    private final MemberStatusRepository memberStatusRepository;
    private final TimeTableRepository timeTableRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ClubRepository clubRepository;

    public MemberStatusService(MemberStatusRepository memberStatusRepository,
                               TimeTableRepository timeTableRepository,
                               UserRepository userRepository,
                               UserProfileRepository userProfileRepository,
                               ClubRepository clubRepository) {
        this.memberStatusRepository = memberStatusRepository;
        this.timeTableRepository = timeTableRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.clubRepository = clubRepository;
    }

    @Transactional
    public void createMemberStatus(Principal principal, Long clubIdx, PostMemberStatusReq memberStatus) throws BaseException {
        try {
            //사용자 정보
            Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
            Long userIdx = user.get().getUserIdx();
            Optional<UserProfileEntity> userProfile = userProfileRepository.findByUserIdx(userIdx);

            //신청하고자 하는 그룹
            Optional<ClubEntity> club = clubRepository.findById(clubIdx);

            //member_status 생성
            MemberStatusEntity memberStatusEntity = MemberStatusEntity.builder()
                    .clubIdx(club.get())
                    .userProfileIdx(userProfile.get())
                    .build();

            memberStatusRepository.save(memberStatusEntity);

            //시간표 등록
            List<TimeTableDTO> timeTables = memberStatus.getTimeTables();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            for (int i = 0; i < timeTables.size(); i++) {
                //1. String -> LocalDateTime 변환
                String startStr = timeTables.get(i).getStart();
                String endStr = timeTables.get(i).getEnd();
                LocalDateTime startTime = LocalDateTime.parse(startStr, formatter);
                LocalDateTime endTime = LocalDateTime.parse(endStr, formatter);

                TimeTableEntity timeTableEntity = TimeTableEntity.builder()
                        .memberStatusIdx(memberStatusEntity)
                        .day(timeTables.get(i).getDay())
                        .start(startTime)
                        .end(endTime)
                        .goal(timeTables.get(i).getGoal())
                        .goalType(timeTables.get(i).getGoalType())
                        .build();

                timeTableRepository.save(timeTableEntity);
            }

        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.POST_MEMBER_STATUS_FAIL);
        }
    }

}

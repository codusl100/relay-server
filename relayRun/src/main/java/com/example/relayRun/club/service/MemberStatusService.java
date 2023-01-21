package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Optional;

@Service
@Transactional
public class MemberStatusService {

    private final MemberStatusRepository memberStatusRepository;
    private UserRepository userRepository;
    private UserProfileRepository userProfileRepository;

    public MemberStatusService(MemberStatusRepository memberStatusRepository) {
        this.memberStatusRepository = memberStatusRepository;
    }

    //principal에 유저 idx?
    public void createMemberStatus(Principal principal, PostMemberStatusReq memberStatus) throws BaseException {
        try {
            Optional<UserEntity> userEntity = userRepository.findByEmail(principal.getName());
            Long userIdx = userEntity.get().getUserIdx();
            Optional<UserProfileEntity> user = userProfileRepository.findByUserIdx(userIdx);

            MemberStatusEntity memberStatusEntity = MemberStatusEntity.builder()
                    .clubIdx(memberStatus.getClubIdx())
                    .userProfileIdx(user.get())
                    .build();

            memberStatusRepository.save(memberStatusEntity);
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.POST_MEMBER_STATUS_FAIL);
        }
    }

}

package com.example.relayRun.fcm.service;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.event.TimeToRunEvent;
import com.example.relayRun.fcm.dto.PostDeviceReq;
import com.example.relayRun.fcm.dto.PostDeviceRes;
import com.example.relayRun.fcm.entity.UserDeviceEntity;
import com.example.relayRun.fcm.repository.UserDeviceRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class FCMService {
    @Value("${fcm.key.path}")
    private String fcmKeyPath;

    @Value("${fcm.key.scope}")
    private String fcmKeyScope;

    @Value("${fcm.key.projectId}")
    private String fcmKeyProjectId;

    UserRepository userRepository;
    UserDeviceRepository userDeviceRepository;
    UserProfileRepository userProfileRepository;
    MemberStatusRepository memberStatusRepository;

    public FCMService(UserRepository userRepository,
                      MemberStatusRepository memberStatusRepository,
                      UserProfileRepository userProfileRepository,
                      UserDeviceRepository userDeviceRepository
    )
    {
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.userProfileRepository = userProfileRepository;
    }
    @PostConstruct
    public void init(){
        try{
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials.fromStream(new FileInputStream(fcmKeyPath))
                                    .createScoped(List.of(fcmKeyScope))
                    )
                    .setProjectId(fcmKeyProjectId)
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options, "com.example.relay");
                log.info("Firebase application has been initialized");
            }
            FirebaseApp.initializeApp(options);
        }catch(IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendMessageByEmail(String email, String body, String title) throws BaseException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_EMAIL);
        UserEntity user = optionalUser.get();
        sendMessageById(user.getUserIdx(), body, title);
    }

    public void sendMessageById(Long userIdx, String body, String title) throws BaseException {
        List<UserDeviceEntity> devices = userDeviceRepository.findAllByUserIdx_UserIdx(userIdx);
        if (devices.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_FCM_TOKEN);
        sendMessagesByToken(devices,body, title);
    }

    private void    sendMessagesByToken(List<UserDeviceEntity> devices, String body, String title) throws BaseException {
        List<Message> messages = devices.stream()
                .map(
                        device-> Message.builder()
                                .setToken(device.getUserDeviceToken())
                                .setNotification( Notification.builder()
                                        .setBody(body)
                                        .setTitle(title)
                                        .build()
                                )
                                .build()
                ).collect(Collectors.toList());
        try{
            FirebaseMessaging.getInstance().sendAll(messages)
                    .getResponses().stream()
                    .map(response -> {
                        if (response.isSuccessful())
                            log.info(response.getMessageId());
                        else
                            log.error(response.getException().getMessage());
                        return response;
                    });
        }catch(FirebaseMessagingException e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_FCM_TOKEN);
        }
    }
    private void    sendMessageByToken(String token, String body, String title) throws BaseException{
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setBody(body)
                        .setTitle(title)
                        .build()
                )
                .build();
        try{
            FirebaseMessaging.getInstance().send(message);
            log.info("알람 요청 성공");
        }catch(FirebaseMessagingException e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_FCM_TOKEN);
        }

    }

   public PostDeviceRes saveDeviceToken(PostDeviceReq req) throws BaseException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(req.getEmail());
        if (optionalUser.isEmpty())
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        UserEntity user = optionalUser.get();
        Optional<UserDeviceEntity> optionalDevice = userDeviceRepository.findByUserDeviceTokenAndUserIdx(req.getUserDeviceID(), user);
        if (optionalDevice.isPresent())
            throw new BaseException(BaseResponseStatus.POST_ALARM_DUPLICATED_TOKEN);
        UserDeviceEntity userDevice = UserDeviceEntity.builder()
                .userDeviceToken(req.getUserDeviceID())
                .userIdx(user)
                .build();
        userDeviceRepository.save(userDevice);
        sendMessageByToken(req.getUserDeviceID(), "디바이스 토큰 저장", "저장 성공");
        return PostDeviceRes.builder()
                .status("성공")
                .build();
    }

    public void deleteDeviceToken(PostDeviceReq req) throws BaseException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(req.getEmail());
        if (optionalUser.isEmpty())
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        UserEntity user = optionalUser.get();
        Optional<UserDeviceEntity> optionalDevice = userDeviceRepository.findByUserDeviceTokenAndUserIdx(req.getUserDeviceID(), user);
        if (optionalDevice.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_FCM_TOKEN);
        UserDeviceEntity userDevice = optionalDevice.get();
        userDeviceRepository.delete(userDevice);
    }

    public void sendTimeToRunMessage(Long memberStatusIdx, LocalTime start) {
        Optional<MemberStatusEntity> optionalMember = memberStatusRepository.findById(memberStatusIdx);
        if (optionalMember.isEmpty()) {
            log.error("유효하지 않은 memberStatusIdx 입니다.");
            return ;
        }
        MemberStatusEntity member = optionalMember.get();
        Optional<UserProfileEntity> optionalProfile = userProfileRepository.findById(member.getUserProfileIdx().getUserProfileIdx());
        if (optionalProfile.isEmpty()) {
            log.error("유효하지 않은 userProfileIdx 입니다.");
            return ;
        }
        UserProfileEntity profile = optionalProfile.get();
        Optional<UserEntity> optionalUser = userRepository.findById(profile.getUserIdx().getUserIdx());
        if (optionalUser.isEmpty()) {
            log.error("유효하지 않은 userIdx 입니다.");
            return ;
        }
        UserEntity user = optionalUser.get();
        try {
            sendMessageById(user.getUserIdx(), "뛸 시간입니다!", start.toString() + "부터 달리기 시작하세요!");
        } catch (BaseException e) {
            log.error(e.getMessage());
        }
    }
}

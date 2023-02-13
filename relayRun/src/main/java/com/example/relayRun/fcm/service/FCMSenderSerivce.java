package com.example.relayRun.fcm.service;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.fcm.dto.NotificationMessage;
import com.example.relayRun.fcm.dto.PostDeviceReq;
import com.example.relayRun.fcm.dto.PostDeviceRes;
import com.example.relayRun.fcm.entity.UserDeviceEntity;
import com.example.relayRun.fcm.repository.UserDeviceRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FCMSenderSerivce {
    @Value("${fcm.key.path}")
    private String fcmKeyPath;

    @Value("${fcm.key.scope}")
    private String fcmKeyScope;

    @Value("${fcm.key.projectId}")
    private String fcmKeyProjectId;
    UserRepository userRepository;
    UserDeviceRepository userDeviceRepository;
    MemberStatusRepository memberStatusRepository;

    public FCMSenderSerivce(
            UserRepository userRepository,
            UserDeviceRepository userDeviceRepository,
            MemberStatusRepository memberStatusRepository
    ){
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.memberStatusRepository = memberStatusRepository;
    }

    @PostConstruct
    public void init(){
        try{
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials.fromStream(new ClassPathResource(fcmKeyPath).getInputStream())
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

    public void sendMessageByEmail(String email, NotificationMessage notificationMessage) throws BaseException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_EMAIL);
        UserEntity user = optionalUser.get();
        sendMessageById(user.getUserIdx(), notificationMessage);
    }

    public void sendMessageById(Long userIdx, NotificationMessage notificationMessage) throws BaseException {
        List<UserDeviceEntity> devices = userDeviceRepository.findAllByUserIdx_UserIdx(userIdx);
        if (devices.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_FCM_TOKEN);
        sendMessagesByToken(devices,notificationMessage);
    }

    public void sendMessageToAll(NotificationMessage notificationMessage) throws BaseException {
        List<UserDeviceEntity> devices = userDeviceRepository.findAll();
        sendMessagesByToken(devices, notificationMessage);
    }

    public void sendMessageToGroup(Long clubIdx, NotificationMessage notificationMessage) throws BaseException {
        List<MemberStatusEntity> members = memberStatusRepository.findAllByClubIdx_ClubIdxAndApplyStatusAndStatus(
                clubIdx, "ACCEPTED", "active"
        );
        if (members.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_ALARM_EMPTY_CLUB);
        List<UserDeviceEntity> tokenList = new ArrayList<>();
        members.forEach(member-> {
            List<UserDeviceEntity> tokens = userDeviceRepository.findAllByUserIdx_UserIdx(
                    member.getUserProfileIdx().getUserIdx().getUserIdx()
            );
            tokenList.addAll(tokens);
        });
        sendMessagesByToken(tokenList, notificationMessage);
    }
    public void    sendMessagesByToken(List<UserDeviceEntity> devices, NotificationMessage notificationMessage) throws BaseException {
        List<Message> messages = devices.stream()
                .map(
                        device-> Message.builder()
                                .setToken(device.getUserDeviceToken())
                                .setNotification( Notification.builder()
                                        .setBody(notificationMessage.getBody())
                                        .setTitle(notificationMessage.getTitle())
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
    public void    sendMessageByToken(String token, NotificationMessage notificationMessage) throws BaseException{
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setBody(notificationMessage.getBody())
                        .setTitle(notificationMessage.getTitle())
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
}

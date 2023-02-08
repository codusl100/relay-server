package com.example.relayRun.fcm.service;

import com.example.relayRun.fcm.dto.PostDeviceReq;
import com.example.relayRun.fcm.dto.PostDeviceRes;
import com.example.relayRun.fcm.entity.UserDeviceEntity;
import com.example.relayRun.fcm.repository.UserDeviceRepository;
import com.example.relayRun.user.entity.UserEntity;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
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
    public FCMService(UserRepository userRepository,
                      UserDeviceRepository userDeviceRepository)
    {
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
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

//    public void sendMessage(List<Long> ids) throws BaseException {
//        List<UserDeviceEntity> devices = userDeviceRepository.findAllByUserIdx_UserIdx(ids);
//        if (devices.isEmpty())
//            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_FCM_TOKEN);
//        List<Message> messages = devices.stream()
//                .map(
//                        device-> Message.builder()
//                                .setToken(device.getUserDeviceToken())
//                                .setNotification( Notification.builder()
//                                        .setBody("Body")
//                                        .setTitle("Title")
//                                        .build()
//                                )
//                                .build()
//                ).collect(Collectors.toList());
//        try{
//            FirebaseMessaging.getInstance().sendAll(messages)
//                    .getResponses().stream()
//                    .map(response -> {
//                        if (response.isSuccessful())
//                            log.info(response.getMessageId());
//                        else
//                            log.error(response.getException().getMessage());
//                        return response;
//                    });
//        }catch(FirebaseMessagingException e) {
//            log.error(e.getMessage());
//            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_FCM_TOKEN);
//        }
//    }
    public void sendMessage(String email) throws BaseException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_EMAIL);
        UserEntity user = optionalUser.get();
        List<UserDeviceEntity> devices = userDeviceRepository.findAllByUserIdx_UserIdx(user.getUserIdx());
        if (devices.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_ALARM_INVALID_FCM_TOKEN);
        List<Message> messages = devices.stream()
                .map(
                        device-> Message.builder()
                                .setToken(device.getUserDeviceToken())
                                .setNotification( Notification.builder()
                                        .setBody("Body")
                                        .setTitle("Title")
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

    private void    sendMessageByToken(String token) throws BaseException{
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setBody("Body")
                        .setTitle("Title")
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
        sendMessageByToken(req.getUserDeviceID());
       userDeviceRepository.save(userDevice);
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
}

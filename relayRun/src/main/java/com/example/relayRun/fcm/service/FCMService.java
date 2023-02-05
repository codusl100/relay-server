package com.example.relayRun.fcm.service;

import com.example.relayRun.fcm.dto.PostDeviceReq;
import com.example.relayRun.fcm.dto.PostDeviceRes;
import com.example.relayRun.fcm.entity.UserDeviceEntity;
import com.example.relayRun.fcm.repository.UserDeviceRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FCMService {
    @Value("{fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;

    @Value("{fcm.key.scope}")
    private String fireBaseScope;

    UserDeviceRepository userDeviceRepository;
    UserRepository userRepository;
    public FCMService(UserDeviceRepository userDeviceRepository,
                      UserRepository userRepository) {
        this.userDeviceRepository = userDeviceRepository;
        this.userRepository = userRepository;
    }
    @PostConstruct
    public void init() throws IOException{
        FileInputStream refreshToken = new FileInputStream(FCM_PRIVATE_KEY_PATH);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(refreshToken))
                .build();
        FirebaseApp.initializeApp(options);
    }

    public void sendByTokenList(List<String> toTokens)
    {

    }
    public PostDeviceRes saveDeviceToken(PostDeviceReq req) throws BaseException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(req.getEmail());
        if (optionalUser.isEmpty())
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        UserEntity user = optionalUser.get();
        UserDeviceEntity userDevice = UserDeviceEntity.builder()
                .userDeviceToken(req.getUserDeviceID())
                .userIdx(user)
                .build();
        userDeviceRepository.save(userDevice);
        return PostDeviceRes.builder()
                .status("성공")
                .build();
    }
}

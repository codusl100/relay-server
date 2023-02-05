package com.example.relayRun.fcm.repository;

import com.example.relayRun.fcm.entity.UserDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeviceRepository extends JpaRepository<UserDeviceEntity, Long> {
}

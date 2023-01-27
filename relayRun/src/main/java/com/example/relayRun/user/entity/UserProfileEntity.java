package com.example.relayRun.user.entity;

import com.example.relayRun.util.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
@Table(name = "user_profile")
public class UserProfileEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProfileIdx;

    @ManyToOne
    @JoinColumn(name = "userIdx")
    private UserEntity userIdx;

    @Column(nullable = false, length = 20)
    private String nickName;

    @Column(columnDefinition = "text")
    private String imgURL;

    @Column(columnDefinition = "text")
    private String statusMsg;

    @Column(columnDefinition = "varchar(1) default 'y'")
    private String isAlarmOn;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    public void changeNickName(String nickName){
        this.nickName = nickName;
    }

    public void changeImgUrl(String imgURL){
        this.imgURL = imgURL;
    }

    public void changeStatusMsg(String statusMsg){
        this.statusMsg = statusMsg;
    }
}


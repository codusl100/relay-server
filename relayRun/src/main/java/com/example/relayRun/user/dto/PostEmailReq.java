package com.example.relayRun.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class PostEmailReq {
    @Email
    private String email;
}

package com.example.relayRun.user.dto;

import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetEmailCodeReq {
    private String code;
}

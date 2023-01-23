package com.example.relayRun.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetEmailCodeReq {
    @ApiModelProperty(example = "인증 번호")
    private String code;
}

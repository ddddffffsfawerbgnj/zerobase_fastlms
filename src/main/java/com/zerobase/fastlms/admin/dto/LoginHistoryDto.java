package com.zerobase.fastlms.admin.dto;

import com.zerobase.fastlms.admin.entity.LoginHistory;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistoryDto {
    private Long id;
    private String userId;
    private LocalDateTime loggedDt;
    private String clientIp;
    private String userAgent;

    public static LoginHistoryDto of(LoginHistory loginHistory) {
        return LoginHistoryDto.builder()
                .id(loginHistory.getId())
                .userId(loginHistory.getUserId())
                .loggedDt(loginHistory.getLoggedDt())
                .clientIp(loginHistory.getClientIp())
                .userAgent(loginHistory.getUserAgent())
                .build();
    }

    public String getLoggedDtText() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return loggedDt != null ? loggedDt.format(dateTimeFormatter) : "";
    }
}

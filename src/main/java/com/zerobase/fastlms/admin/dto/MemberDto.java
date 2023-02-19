package com.zerobase.fastlms.admin.dto;

import com.zerobase.fastlms.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private String userId;

    private String userName;
    private String phone;
    private String password;

    private LocalDateTime regDt;
    private LocalDateTime updDt;

    private Boolean emailAuthYn;
    private LocalDateTime emailAuthDt;
    private String emailAuthKey;

    private String resetPasswordKey;
    private LocalDateTime resetPasswordLimitDt;

    private Boolean adminYn;
    private String userStatus;

    //추가 컬럼
    private Long totalCount;
    private Long seq;

    private String zipcode;
    private String addr;
    private String addrDetail;

    private LocalDateTime lastLoginDt;
    
    public static MemberDto of(Member member) {
        
        return MemberDto.builder()
                .userId(member.getUserId())
                .userName(member.getUserName())
                .phone(member.getPhone())
                //.password(member.getPassword())
                .regDt(member.getRegDt())
                .updDt(member.getUpdDt())
                .emailAuthYn(member.getEmailAuthYn())
                .emailAuthDt(member.getEmailAuthDt())
                .emailAuthKey(member.getEmailAuthKey())
                .resetPasswordKey(member.getResetPasswordKey())
                .resetPasswordLimitDt(member.getResetPasswordLimitDt())
                .adminYn(member.getAdminYn())
                .userStatus(member.getUserStatus())
                
                .zipcode(member.getZipcode())
                .addr(member.getAddr())
                .addrDetail(member.getAddrDetail())
                
                .build();
    }
    
    
    public String getRegDtText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return regDt != null ? regDt.format(formatter) : "";
    }
    
    public String getUdtDtText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return updDt != null ? updDt.format(formatter) : "";
        
    }
    public String getLastLoginDtText() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return lastLoginDt != null ? lastLoginDt.format(dateTimeFormatter) : "";
    }
    
}

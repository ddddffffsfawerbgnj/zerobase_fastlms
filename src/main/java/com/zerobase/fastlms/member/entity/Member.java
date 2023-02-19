package com.zerobase.fastlms.member.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member implements MemberCode {

    @Id
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
    //유효한 기간
    private LocalDateTime resetPasswordLimitDt;

    //관리자 여부 지정
    private Boolean adminYn;
    //회원 상태
    //이용가능한 상태 , 정지상태
    private String userStatus;

    private String zipcode;
    private String addr;
    private String addrDetail;

}

package com.zerobase.fastlms.admin.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //배너명
    private String subject;
    //배너 파일
    private String fileName;
    private String urlFileName;
    //링크 주소
    private String clickedPath;
    //오픈 방법
    private String openMethod;
    //정렬 순서
    private String sortValue;
    //공개 여부
    private Boolean postYn;
    //강좌명
    private String courseSubject;
    //강좌 아이디
    private Long courseId;

    private LocalDateTime regDt;
    private LocalDateTime updDt;
}
package com.zerobase.fastlms.admin.dto;

import com.zerobase.fastlms.admin.entity.Banner;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerDto {
    private Long id;
    private String subject;
    private String fileName;
    private String urlFileName;
    private String clickedPath;
    private String openMethod;
    private String sortValue;
    private Boolean postYn;
    private String courseSubject;
    private Long courseId;

    private LocalDateTime regDt;
    private LocalDateTime updDt;

    private Long totalCount;
    private Long seq;

    public static BannerDto of(Banner banner) {
        return BannerDto.builder()
                .id(banner.getId())
                .subject(banner.getSubject())
                .fileName(banner.getFileName())
                .urlFileName(banner.getUrlFileName())
                .clickedPath(banner.getClickedPath())
                .openMethod(banner.getOpenMethod())
                .sortValue(banner.getSortValue())
                .postYn(banner.getPostYn())
                .courseId(banner.getCourseId())
                .courseSubject(banner.getCourseSubject())
                .regDt(banner.getRegDt())
                .updDt(banner.getUpdDt())
                .build();
    }

    public String getRegDtText() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return regDt != null ? regDt.format(dateTimeFormatter) : "";
    }
}

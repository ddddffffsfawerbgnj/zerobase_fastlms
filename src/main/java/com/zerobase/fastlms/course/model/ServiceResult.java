package com.zerobase.fastlms.course.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResult {

    Boolean result;
    String message;

}

package com.zerobase.fastlms.course.model;

import com.zerobase.fastlms.admin.model.CommonParam;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TakeCourseParam extends CommonParam {

    long id;
    String status;
    
    String userId;
    
    
    long searchCourseId;
}

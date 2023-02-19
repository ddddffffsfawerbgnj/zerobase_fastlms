package com.zerobase.fastlms.admin.service;

import com.zerobase.fastlms.admin.dto.BannerDto;
import com.zerobase.fastlms.admin.model.BannerInput;
import com.zerobase.fastlms.admin.model.BannerParam;
import com.zerobase.fastlms.course.model.ServiceResult;

import java.util.List;

public interface BannerService {

    ServiceResult add(BannerInput parameter);


    ServiceResult set(BannerInput parameter);

    List<BannerDto> list(BannerParam courseParam);

    BannerDto getById(Long id);

    Boolean del(String idList);

    List<BannerDto> frontList();
}

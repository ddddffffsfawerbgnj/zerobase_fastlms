package com.zerobase.fastlms.admin.service.impl;

import com.zerobase.fastlms.admin.dto.BannerDto;
import com.zerobase.fastlms.admin.entity.Banner;
import com.zerobase.fastlms.admin.mapper.BannerMapper;
import com.zerobase.fastlms.admin.model.BannerInput;
import com.zerobase.fastlms.admin.model.BannerParam;
import com.zerobase.fastlms.admin.repository.BannerRepository;
import com.zerobase.fastlms.admin.service.BannerService;
import com.zerobase.fastlms.course.entity.Course;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    private final CourseRepository courseRepository;

    private final BannerMapper bannerMapper;

    @Override
    public ServiceResult add(BannerInput parameter) {
        Optional<Course> optionalCourse = courseRepository.findById(parameter.getCourseId());
        if (!optionalCourse.isPresent()) {
            return new ServiceResult(false, "해당 강좌가 존재하지 않습니다.");
        }

        /**
         * 링크 주소에 /course/입력시.. 강좌 상세목록으로 가게 하기 위해
         * courseId를 추가해주는 로직
         */
        String path = parameter.getClickedPath();
        if ("/course/".equals(path)) {
            path += parameter.getCourseId();
        }


        bannerRepository.save(
                Banner.builder()
                        .subject(parameter.getSubject())
                        .fileName(parameter.getFileName())
                        .urlFileName(parameter.getUrlFileName())
                        .clickedPath(path)
                        .openMethod(parameter.getOpenMethod())
                        .sortValue(parameter.getSortValue())
                        .postYn(parameter.getPostYn())
                        .courseId(parameter.getCourseId())
                        .courseSubject(optionalCourse.get().getSubject())
                        .regDt(LocalDateTime.now())
                        .build()
        );
        return new ServiceResult(true, "");
    }

    @Override
    public ServiceResult set(BannerInput parameter) {
        Optional<Banner> optionalBanner = bannerRepository.findById(parameter.getId());
        if (!optionalBanner.isPresent()) {
            return new ServiceResult(false, "배너 정보가 존재하지 않습니다.");
        }

        Optional<Course> optionalCourse = courseRepository.findById(parameter.getCourseId());
        if (!optionalCourse.isPresent()) {
            return new ServiceResult(false, "해당 강좌가 존재하지 않습니다.");
        }

        /**
         * 링크 주소에 /course/입력시.. 강좌 상세목록으로 가게 하기 위해
         * courseId를 추가해주는 로직
         */
        String path = parameter.getClickedPath();
        if ("/course/".equals(path)) {
            path += parameter.getCourseId();
        }

        Banner banner = optionalBanner.get();

        banner.setSubject(parameter.getSubject());
        banner.setClickedPath(path);
        banner.setOpenMethod(parameter.getOpenMethod());
        banner.setSortValue(parameter.getSortValue());
        banner.setPostYn(parameter.getPostYn());
        banner.setFileName(parameter.getFileName());
        banner.setUrlFileName(parameter.getUrlFileName());
        banner.setCourseId(parameter.getCourseId());
        banner.setCourseSubject(optionalCourse.get().getSubject());
        banner.setUpdDt(LocalDateTime.now());

        bannerRepository.save(banner);

        return new ServiceResult(true, "");
    }

    @Override
    public List<BannerDto> list(BannerParam parameter) {

        long totalCount = bannerMapper.selectListCount(parameter);
        List<BannerDto> list = bannerMapper.selectList(parameter);

        if (!CollectionUtils.isEmpty(list)) {
            int i = 1;
            for (BannerDto dto : list) {
                dto.setTotalCount(totalCount);
                dto.setSeq(parameter.getPageStart() + i);
                i++;
            }
        }
        return list;
    }

    @Override
    public BannerDto getById(Long id) {
        return bannerRepository.findById(id).map(BannerDto::of).orElse(null);
    }

    @Override
    public List<BannerDto> frontList() {
        return bannerMapper.selectListPost();
    }

    @Override
    public Boolean del(String idList) {
        if (idList != null && idList.length() > 0) {
            for (String id : idList.split(",")) {
                Long _id = 0L;

                try {
                    _id = Long.parseLong(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (_id > 0) {
                    bannerRepository.deleteById(_id);
                }
            }
        }
        return true;
    }
}

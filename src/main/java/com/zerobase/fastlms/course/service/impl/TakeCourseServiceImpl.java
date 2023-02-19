package com.zerobase.fastlms.course.service.impl;

import com.zerobase.fastlms.course.dto.TakeCourseDto;
import com.zerobase.fastlms.course.entity.TakeCourse;
import com.zerobase.fastlms.course.mapper.TakeCourseMapper;
import com.zerobase.fastlms.course.model.*;
import com.zerobase.fastlms.course.repository.TakeCourseRepository;
import com.zerobase.fastlms.course.service.TakeCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TakeCourseServiceImpl implements TakeCourseService {

    private final TakeCourseRepository takeCourseRepository;
    private final TakeCourseMapper takeCourseMapper;

    @Override
    public List<TakeCourseDto> list(TakeCourseParam parameter) {
        Long totalCount = takeCourseMapper.selectListCount(parameter);
        List<TakeCourseDto> list = takeCourseMapper.selectList(parameter);
        if (!CollectionUtils.isEmpty(list)) {
            int i = 1;
            for (TakeCourseDto dto : list) {
                dto.setTotalCount(totalCount);
                dto.setSeq(parameter.getPageStart() + i);
                i++;
            }
        }
        return list;
    }

    @Override
    public TakeCourseDto detail(long id) {
        return takeCourseRepository.findById(id)
                .map(TakeCourseDto::of).orElse(null);
    }

    @Override
    public ServiceResult updateStatus(long id, String status) {
        Optional<TakeCourse> optionalTakeCourse = takeCourseRepository.findById(id);
        if (!optionalTakeCourse.isPresent()) {
            return new ServiceResult(false, "수강정보가 존재하지 않습니다.");
        }

        TakeCourse takeCourse = optionalTakeCourse.get();
        takeCourse.setStatus(status);
        takeCourseRepository.save(takeCourse);


        return new ServiceResult(true, "");
    }

    @Override
    public List<TakeCourseDto> myCourse(String userId) {
        return takeCourseMapper.selectListMyCourse(TakeCourseParam.builder()
                .userId(userId)
                .build()
        );
    }

    @Override
    public ServiceResult cancel(long id) {
        return null;
    }

    @Override
    public ServiceResult cancel(Long id) {
        Optional<TakeCourse> optionalTakeCourse = takeCourseRepository.findById(id);
        if (!optionalTakeCourse.isPresent()) {
            return new ServiceResult(false, "수강정보가 존재하지 않습니다.");
        }

        TakeCourse takeCourse = optionalTakeCourse.get();
        takeCourse.setStatus(TakeCourse.STATUS_CANCEL);
        takeCourseRepository.save(takeCourse);

        return new ServiceResult(true, "");
    }
}
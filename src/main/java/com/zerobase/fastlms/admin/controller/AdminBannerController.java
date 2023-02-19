package com.zerobase.fastlms.admin.controller;


import com.zerobase.fastlms.admin.dto.BannerDto;
import com.zerobase.fastlms.admin.model.BannerInput;
import com.zerobase.fastlms.admin.model.BannerParam;
import com.zerobase.fastlms.admin.service.BannerService;
import com.zerobase.fastlms.course.dto.CourseDto;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.course.service.CourseService;
import com.zerobase.fastlms.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminBannerController extends BaseController {

    private final BannerService bannerService;

    private final CourseService courseService;

    @GetMapping("/admin/banner/list.do")
    public String list(Model model, BannerParam bannerParam) {

        bannerParam.init();

        List<BannerDto> bannerList = bannerService.list(bannerParam);

        long totalCount = 0;
        if (!CollectionUtils.isEmpty(bannerList)) {
            totalCount = bannerList.get(0).getTotalCount();
        }

        String pagetHtml = getPagerHtml(totalCount, bannerParam.getPageSize(),
                bannerParam.getPageIndex(), bannerParam.getQueryString());

        model.addAttribute("list", bannerList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pager", pagetHtml);

        return "admin/banner/list";
    }

    @GetMapping(value = {"/admin/banner/add.do", "/admin/banner/edit.do"})
    public String add(
            Model model
            , HttpServletRequest request
            , BannerInput parameter
    ) {

        boolean editMode = request.getRequestURI().contains("/edit.do");
        BannerDto detail = new BannerDto();

        if (editMode) {
            Long id = parameter.getId();
            BannerDto existBanner = bannerService.getById(id);
            if (existBanner == null) {
                //error처리
                model.addAttribute("errorMessage", "배너 정보가 존재하지 않습니다.");
                return "common/error";
            }
            detail = existBanner;
        }
        model.addAttribute("detail", detail);
        model.addAttribute("editMode", editMode);

        List<CourseDto> courseList = courseService.listAll();
        model.addAttribute("courseList", courseList);

        return "admin/banner/add";
    }

    @PostMapping(value = {"/admin/banner/add.do", "/admin/banner/edit.do"})
    public String addSubmit(Model model
            , HttpServletRequest request
            , BannerInput parameter
            , MultipartFile file
    ) {

        String saveFileName = "";
        String urlFileName = "";
        Boolean editMode = request.getRequestURI().contains("/edit.do");

        if (file != null) {
            /**
             * Edit 시 오류 해결을 위해 추가
             */
            String originalFileName = file.getOriginalFilename();
            boolean isFileSame = false;

            if (editMode) {
                System.out.println("origin >> " + originalFileName);
                BannerDto bannerDto = bannerService.getById(parameter.getId());
                String exFileName = bannerDto.getUrlFileName();
                System.out.println("ex >> " + exFileName);
                int dotPos = exFileName.lastIndexOf(".");
                if (originalFileName == null || originalFileName.equals("")
                        || (dotPos > 0 && (originalFileName).equals(exFileName.substring(0, dotPos)))) {
                    originalFileName = exFileName;
                    isFileSame = true;
                    parameter.setFileName(bannerDto.getFileName());
                    parameter.setUrlFileName(bannerDto.getUrlFileName());
                }
            }
            if (!isFileSame) {
                String baseLocalPath = "/Users/a/Desktop/Spring파일/minicampus/minicampus/files";
                String baseUrlPath = "/files";
                String[] arrFileName = FileUtil.getNewSaveFile(baseLocalPath, baseUrlPath, originalFileName);

                saveFileName = arrFileName[0];
                urlFileName = arrFileName[1];


                try {
                    File newFile = new File(saveFileName);
                    FileCopyUtils.copy(file.getInputStream(), Files.newOutputStream(newFile.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
                parameter.setFileName(saveFileName);
                parameter.setUrlFileName(urlFileName);
            }
        }

        ServiceResult result = new ServiceResult();
        if (editMode) {
            Long id = parameter.getId();
            BannerDto existBanner = bannerService.getById(id);
            if (existBanner == null) {
                //error처리
                model.addAttribute("errorMessage", "배너 정보가 존재하지 않습니다.");
                return "common/error";
            }
            result = bannerService.set(parameter);
        } else {
            result = bannerService.add(parameter);
        }

        if (!result.getResult()) {
            //error처리
            model.addAttribute("errorMessage", result.getMessage());
            return "common/error";
        }

        return "redirect:/admin/banner/list.do";
    }

    @PostMapping("/admin/banner/delete.do")
    public String del(BannerInput parameter) {
        Boolean result = bannerService.del(parameter.getIdList());
        return "redirect:/admin/banner/list.do";
    }

}

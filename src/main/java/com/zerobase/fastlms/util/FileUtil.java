package com.zerobase.fastlms.util;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;

public class FileUtil {

    public static String[] getNewSaveFile(String basePath, String baseUrlPath, String originalFileName) {
        LocalDate now = LocalDate.now();
        String[] dirs = {
                String.format("%s/%d/", basePath, now.getYear())
                , String.format("%s/%d/%02d/", basePath, now.getYear(), now.getMonthValue())
                , String.format("%s/%d/%02d/%02d/", basePath, now.getYear(), now.getMonthValue(), now.getDayOfMonth())
        };

        String urlDir = String.format("%s/%d/%02d/%02d/", baseUrlPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.isDirectory()) {
                file.mkdir();
            }
        }

        String fileExtension = "";
        if (originalFileName != null) {
            int dotPos = originalFileName.lastIndexOf(".");
            if (dotPos > -1) {
                fileExtension = originalFileName.substring(dotPos + 1);
            }
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String newFileName = String.format("%s%s", dirs[2], uuid);
        String newUrlFileName = String.format("%s%s", urlDir, uuid);
        //확장자가 있을 때..
        if (fileExtension.length() > 0) {
            newFileName += "." + fileExtension;
            newUrlFileName += "." + fileExtension;
        }

        return new String[]{newFileName, newUrlFileName};
    }
}

package com.campus.framework.untils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 提供文件路径生成功能的工具类
 */
public class PathUtils {

    /**
     * 根据文件名生成对应的文件路径
     *
     * @param fileName 原始文件名，用于提取文件后缀
     * @return 生成的文件路径，包含日期目录、去重的文件名和后缀
     */
    public static String generateFilePath(String fileName){
        // 根据日期生成路径，格式如：2022/1/15/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String datePath = sdf.format(new Date());

        // 使用UUID作为文件名，以减少重名冲突
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // 获取文件后缀，包括.在内
        int index = fileName.lastIndexOf(".");
        String fileType = fileName.substring(index);

        // 拼接日期路径、UUID和文件后缀，生成最终的文件路径
        return new StringBuilder().append(datePath).append(uuid).append(fileType).toString();
    }
}

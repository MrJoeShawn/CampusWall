package com.campus.admin.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;


    /**
     * 用户修改头像
     * @param imgName
     * @return
     */
    @PostMapping("/uploadUserHeaderImg")
    public ResponseResult uploadHeaderImg(@RequestParam("imgName") MultipartFile imgName) {
        return uploadService.uploadHeaderImg(imgName);
    }


    /**
     * 获取上传凭证
     * @return
     */
    @GetMapping("/qiniutoken")
    public ResponseResult getUploadToken() {
        String upToken = uploadService.getUploadToken();
        return ResponseResult.okResult(upToken);
    }
}

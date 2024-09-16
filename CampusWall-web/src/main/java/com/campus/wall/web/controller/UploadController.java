package com.campus.wall.web.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;


    @PostMapping("/uploadUserHeaderImg")
    public ResponseResult uploadHeaderImg(@RequestParam("imgName") MultipartFile imgName) {
        return uploadService.uploadHeaderImg(imgName);
    }
}

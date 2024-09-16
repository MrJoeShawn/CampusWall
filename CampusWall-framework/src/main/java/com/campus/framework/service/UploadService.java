package com.campus.framework.service;

import com.campus.framework.dao.repository.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    ResponseResult uploadHeaderImg(MultipartFile imgName);
}


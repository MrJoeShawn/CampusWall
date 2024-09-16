package com.campus.framework.service.impl;

import com.campus.framework.dao.entity.LoginUser;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.mapper.UsersMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.UploadService;
import com.campus.framework.untils.PathUtils;
import com.campus.framework.untils.RedisCache;
import com.campus.framework.untils.SecurityUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Data
@ConfigurationProperties(prefix = "oss")
public class UploadServiceImpl implements UploadService {

    @Autowired
    private RedisCache redisCache; // Redis 缓存工具类

    @Autowired
    private UsersMapper usersMapper; // 直接操作 MySQL 用户数据

    private String accessKey;
    private String secretKey;
    private String bucket;

    /**
     * 上传头像并更新用户信息
     */
    @Override
    public ResponseResult uploadHeaderImg(MultipartFile imgName) {
        Integer userId = SecurityUtils.getUserId();

        // 校验文件类型
        String originalFilename = imgName.getOriginalFilename();
        if (!isImageFile(originalFilename)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INVALID_IMAGE_FORMAT, "只支持 .png 和 .jpg 格式的图片");
        }

        // 生成上传文件路径并上传到七牛云
        String filePath = PathUtils.generateFilePath(originalFilename);
        String url = uploadToOss(imgName, filePath);
        if (url == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.UPLOAD_FAILED, "文件上传失败");
        }

        // 更新 MySQL 中的用户头像信息
        Users user = new Users();
        user.setId(userId);
        user.setProfilePictureUrl(url);
        int result = usersMapper.updateById(user);
        if (result <= 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.UPDATE_FAILED, "头像更新失败");
        }

        // 从 Redis 中获取 LoginUser 对象
        String redisKey = "login:" + userId;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if (loginUser != null) {
            // 更新 LoginUser 对象中的头像 URL
            loginUser.getUsers().setProfilePictureUrl(url);
            // 重新将更新后的 LoginUser 对象存入 Redis
            redisCache.setCacheObject(redisKey, loginUser);
        }

        return ResponseResult.okResult(url, "头像上传并更新成功");
    }


    /**
     * 校验是否是支持的图片文件格式
     */
    private boolean isImageFile(String filename) {
        return filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg");
    }

    /**
     * 上传文件到七牛云
     */
    public String uploadToOss(MultipartFile imgName, String filePath) {
        // 构造七牛云配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;

        UploadManager uploadManager = new UploadManager(cfg);
        String key = filePath;

        try (InputStream inputStream = imgName.getInputStream()) {
            // 获取七牛云上传凭证
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            // 上传文件
            Response response = uploadManager.put(inputStream, key, upToken, null, null);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);

            // 返回文件的外链 URL
            return "http://sjl9uge8l.hn-bkt.clouddn.com/" + key;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

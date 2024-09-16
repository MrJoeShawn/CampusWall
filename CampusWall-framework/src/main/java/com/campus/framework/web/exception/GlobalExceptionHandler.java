package com.campus.framework.web.exception;

import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.repository.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，用于处理系统中抛出的异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理所有异常
     *
     * @param e 异常对象
     * @return 包含错误代码和消息的 ResponseResult 对象
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult handleException(Exception e) {
        log.error("出现了异常！", e);  // 输出详细异常堆栈
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "系统异常，请联系管理员");
    }
}

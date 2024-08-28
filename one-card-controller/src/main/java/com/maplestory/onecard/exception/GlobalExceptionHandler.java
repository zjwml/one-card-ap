package com.maplestory.onecard.exception;

import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理Validated验证异常
     *
     * @param e 错误
     * @return 通用返回
     */
    @ExceptionHandler({BindException.class})
    public ResponseJson<Object> bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError error : fieldErrors) {
            errorMsg.append("[");
            errorMsg.append(error.getDefaultMessage());
            errorMsg.append("]");
        }
        return ResponseJson.failure(OneCardConstant.Code_OtherFail, errorMsg.toString());
    }

    /**
     * 处理NullPointerException异常
     *
     * @param e 错误
     * @return 通用返回
     */
    @ExceptionHandler({NullPointerException.class})
    public ResponseJson<Object> customExceptionHandler(NullPointerException e) {
        log.error("NullPointerException异常：", e);
        return ResponseJson.error("空指针异常");
    }

    /**
     * 处理其他异常
     *
     * @param e 错误
     * @return 通用返回
     */
    @ExceptionHandler({Exception.class})
    public ResponseJson<Object> exceptionHandler(Exception e) {
        log.error("Exception异常：", e);
        return ResponseJson.failure(OneCardConstant.Code_OtherFail, e.getMessage());
    }
}

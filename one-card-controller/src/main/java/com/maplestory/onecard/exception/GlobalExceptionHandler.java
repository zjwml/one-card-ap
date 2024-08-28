package com.maplestory.onecard.exception;

import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理Validated验证异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({BindException.class})
    public ResponseJson<Object> bindExceptionHandler(BindException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        log.error("BindException：", e);
        return ResponseJson.failure(OneCardConstant.Code_OtherFail, objectError.getDefaultMessage());
    }

    /**
     * 处理NullPointerException异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({NullPointerException.class})
    public ResponseJson<Object> customExceptionHandler(NullPointerException e) {
        log.error("NullPointerException异常：", e);
        return ResponseJson.error("空指针异常");
    }

    /**
     * 处理其他异常
     * @param e
     * @return
     */
    @ExceptionHandler({Exception.class})
    public ResponseJson<Object> exceptionHandler(Exception e) {
        log.error("Exception异常：", e);
        return ResponseJson.failure(OneCardConstant.Code_OtherFail, e.getMessage());
    }
}

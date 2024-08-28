package com.maplestory.onecard.controller;

import com.maplestory.onecard.service.service.UserLogin;
import com.maplestory.onecard.service.vo.ResponseJson;
import com.maplestory.onecard.service.vo.UserLoginInVo;
import com.maplestory.onecard.service.vo.UserLoginOutVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {

    private static final String log001 = "UserController happened:";

    StringBuffer errMsg = new StringBuffer();

    @Autowired
    private UserLogin userLogin;

    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseJson<UserLoginOutVo> login(@RequestBody @Valid UserLoginInVo inVo, BindingResult bindingResult, HttpServletRequest request) throws Exception {

        return userLogin.doService(inVo);
    }

}

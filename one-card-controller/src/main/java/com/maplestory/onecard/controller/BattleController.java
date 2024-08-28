package com.maplestory.onecard.controller;

import com.maplestory.onecard.service.service.UserLogin;
import com.maplestory.onecard.service.vo.ResponseJson;
import com.maplestory.onecard.service.vo.UserLoginInVo;
import com.maplestory.onecard.service.vo.UserLoginOutVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/battle")
@RestController
@Slf4j
public class BattleController {

    private static final String log001 = "UserController happened:";

    StringBuffer errMsg = new StringBuffer();

    @Autowired
    private UserLogin userLogin;

//    @PostMapping(value = "/start")
//    @ResponseBody
//    public ResponseJson<UserLoginOutVo> login(@Valid @RequestBody UserLoginInVo inVo, BindingResult bindingResult) throws Exception {
//        if (bindingResult.hasErrors()) {
//            return ResponseJson.requestError(bindingResult.getAllErrors().get(0).getDefaultMessage());
//        }
//        ResponseJson<UserLoginOutVo> outVo = new ResponseJson<>();
//        try {
//            outVo = userLogin.doService(inVo);
//        } catch (Exception e) {
//            errMsg.append("调用子交易发生错误:===").append(e);
//        }
//
//        if (errMsg.length() != 0) {
//            log.error(log001 + errMsg);
//        }
//
//        return outVo;
//    }

}

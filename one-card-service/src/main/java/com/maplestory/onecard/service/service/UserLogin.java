package com.maplestory.onecard.service.service;

import com.maplestory.onecard.service.vo.UserLoginInVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import com.maplestory.onecard.service.vo.UserLoginOutVo;

public interface UserLogin {

    ResponseJson<UserLoginOutVo>doService(UserLoginInVo inVo);
}

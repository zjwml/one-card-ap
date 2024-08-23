package com.maplestory.onecard.service;

import com.maplestory.onecard.vo.ResponseJson;
import com.maplestory.onecard.vo.UserLoginInVo;
import com.maplestory.onecard.vo.UserLoginOutVo;

public interface UserLogin {

    ResponseJson<UserLoginOutVo>doService(UserLoginInVo inVo);
}

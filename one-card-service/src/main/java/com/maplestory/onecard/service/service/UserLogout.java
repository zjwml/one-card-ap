package com.maplestory.onecard.service.service;

import com.maplestory.onecard.service.vo.ResponseJson;
import com.maplestory.onecard.service.vo.UserLogoutInVo;
import com.maplestory.onecard.service.vo.UserLogoutOutVo;

public interface UserLogout {

    ResponseJson<UserLogoutOutVo>doService(UserLogoutInVo inVo);
}

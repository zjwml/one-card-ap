package com.maplestory.onecard.service.Impl;

import com.maplestory.onecard.constant.OneCardConstant;
import com.maplestory.onecard.domain.UserInfo;
import com.maplestory.onecard.mapper.UserInfoMapper;
import com.maplestory.onecard.service.UserLogin;
import com.maplestory.onecard.vo.ResponseJson;
import com.maplestory.onecard.vo.UserLoginInVo;
import com.maplestory.onecard.vo.UserLoginOutVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserLoginImpl implements UserLogin {
    String log001 = "UserLoginImpl happened:";

    private final UserInfoMapper userInfoMapper;

    public UserLoginImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public ResponseJson<UserLoginOutVo> doService(UserLoginInVo inVo) {
        log.info("{}----------交易开始--------", log001);

        UserInfo userInfo = new UserInfo();
        try {
            userInfo = userInfoMapper.selectByUserId(inVo.getUserId());
        } catch (Exception e) {
            log.error("{}--------查询失败:{}-----", log001, e);
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "查询用户失败" + e.getMessage());
        }
        if (StringUtils.isBlank(userInfo.getUserId())) {
            log.info("{}-------新人！插入------", log);
            try {
                UserInfo record = new UserInfo();
                record.setUserId(inVo.getUserId());
                record.setUserName(inVo.getUserName());
                record.setStatus();
                int id = userInfoMapper.insertSelective(record);
            } catch (Exception e) {
                log.error("{}--------查询失败:{}-----", log001, e);
                return ResponseJson.failure(OneCardConstant.Code_OtherFail, "查询用户失败" + e.getMessage());
            }
        }
        return null;
    }
}

package com.maplestory.onecard.service.service.Impl;

import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.mapper.BattleInfoMapper;
import com.maplestory.onecard.service.util.BeanUtils;
import com.maplestory.onecard.service.util.ListUtils;
import com.maplestory.onecard.service.vo.UserLoginInVo;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.model.mapper.UserInfoMapper;
import com.maplestory.onecard.service.service.UserLogin;
import com.maplestory.onecard.service.vo.ResponseJson;
import com.maplestory.onecard.service.vo.UserLoginOutVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserLoginImpl extends CommonService implements UserLogin {
    String log001 = "UserLoginImpl happened:";

    @Override
    public ResponseJson<UserLoginOutVo> doService(UserLoginInVo inVo) {
        log.info("{}----------交易开始--------", log001);

        UserInfo userInfo = userInfoMapper.selectByUserName(inVo.getUserName());
        if (null == userInfo) {
            log.info("{}-------新人！插入------", log);
            try {
                userInfo = new UserInfo();
                userInfo.setUserName(inVo.getUserName());
                userInfo.setStatus(OneCardConstant.User_Status_Available);
                int id = userInfoMapper.insertSelective(userInfo);
            } catch (Exception e) {
                log.error("{}--------插入失败:{}-----", log001, e);
                return ResponseJson.failure(OneCardConstant.Code_OtherFail, "查询用户失败");
            }
        }

        List<BattleInfo> battleInfoList = battleInfoMapper.selectByRoomNumber(inVo.getRoomNumber());
        //开始插入房间
        BattleInfo battleInfo = new BattleInfo();
        if (battleInfoList.size() == 0) {
            //如果是新房间，则是房主
            battleInfo = new BattleInfo();
            battleInfo.setPlayers(String.valueOf(userInfo.getId()));
            battleInfo.setRoomNumber(inVo.getRoomNumber());

            int id = battleInfoMapper.insertSelective(battleInfo);

        } else if (battleInfoList.size() == 1) {
            battleInfo = battleInfoList.get(0);
            List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
            //判断情况
            if (players.size() == 4) {
                log.info("{}--------房间已满-----", log001);
                return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间已满");
            }
            if (players.contains(userInfo.getId().toString())) {
                log.info("{}--------已在桌上-----", log001);
            } else {
                players.add(userInfo.getId().toString());
                battleInfo.setPlayers(ListUtils.StringListToString(players));

                int id = battleInfoMapper.updateByPrimaryKeySelective(battleInfo);
            }

        } else {
            log.error("{}--------房间{}不唯一！-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间冲突！请联系管理员");
        }

        UserLoginOutVo outVo = new UserLoginOutVo();
        outVo.setBattleInfoSubOutVo(BeanUtils.switchNullToEmpty(getBattleInfoSubOutVo(battleInfo, userInfo)));
        outVo.setUserInfo(BeanUtils.switchNullToEmpty(userInfo));
        return ResponseJson.ok(outVo);
    }

}

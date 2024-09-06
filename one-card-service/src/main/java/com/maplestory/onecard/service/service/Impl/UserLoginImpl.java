package com.maplestory.onecard.service.service.Impl;

import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.service.util.BeanUtils;
import com.maplestory.onecard.service.vo.UserLoginInVo;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.service.service.UserLogin;
import com.maplestory.onecard.service.vo.ResponseJson;
import com.maplestory.onecard.service.vo.UserLoginOutVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserLoginImpl extends CommonService implements UserLogin {
    String log001 = "UserLoginImpl happened:";

    @SneakyThrows
    @Override
    public ResponseJson<UserLoginOutVo> doService(UserLoginInVo inVo) {
        log.info("{}----------交易开始--------", log001);

        String userName = inVo.getUserName().trim();
        UserInfo userInfo = userInfoMapper.selectByUserName(userName);
        if (null == userInfo) {
            log.info("{}-------新人！插入------", log);
            try {
                userInfo = new UserInfo();
                userInfo.setUserName(inVo.getUserName());
                userInfo.setStatus(OneCardConstant.User_Status_Available);
                userInfo.setPpp(inVo.getPpp());
                userInfoMapper.insertSelective(userInfo);
            } catch (Exception e) {
                log.error("{}--------插入失败:{}-----", log001, e);
                return ResponseJson.failure(OneCardConstant.Code_OtherFail, "查询用户失败");
            }
        }
        if (!inVo.getPpp().equals(userInfo.getPpp())) {
            log.error("{}--------密码错误-----", log001);
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "密码错误");
        }

        List<BattleInfo> battleInfoList = battleInfoMapper.selectByRoomNumber(inVo.getRoomNumber());
        //开始插入房间
        BattleInfo battleInfo;
        if (battleInfoList.size() == 0) {
            //如果是新房间，则是房主
            battleInfo = new BattleInfo();
            List<UserInfo> list = new ArrayList<>();
            list.add(userInfo);
            battleInfo.setPlayers(objectMapper.writeValueAsString(list));
            battleInfo.setRoomNumber(inVo.getRoomNumber());

            battleInfoMapper.insertSelective(battleInfo);

        } else if (battleInfoList.size() == 1) {
            battleInfo = battleInfoList.get(0);
            List<UserInfo> players = objectMapper.readValue(battleInfo.getPlayers(), objectMapper.getTypeFactory().constructParametricType(List.class, UserInfo.class));
            int pos = getPos(players, userInfo);
            if (pos == -1) {
                if (!OneCardConstant.Battle_Status_waiting.equals(battleInfo.getStatus())) {
                    return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间已开始游戏");
                }
                //判断情况
                if (players.size() == 4) {
                    log.info("{}--------房间已满-----", log001);
                    return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间已满");
                }
                players.add(userInfo);
                battleInfo.setPlayers(objectMapper.writeValueAsString(players));
                battleInfoMapper.updateByPrimaryKeySelective(battleInfo);
            }
        } else {
            log.error("{}--------房间{}不唯一！-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间冲突！请联系管理员");
        }

        UserLoginOutVo outVo = new UserLoginOutVo();
        outVo.setBattleInfoSubOutVo(getBattleInfoSubOutVo(battleInfo, userInfo));
        outVo.setUserInfo(BeanUtils.switchNullToEmpty(userInfo));
        return ResponseJson.ok(outVo);
    }

}

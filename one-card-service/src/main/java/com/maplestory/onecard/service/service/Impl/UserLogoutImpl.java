package com.maplestory.onecard.service.service.Impl;

import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.service.UserLogout;
import com.maplestory.onecard.service.vo.ResponseJson;
import com.maplestory.onecard.service.vo.UserLogoutInVo;
import com.maplestory.onecard.service.vo.UserLogoutOutVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserLogoutImpl extends CommonService implements UserLogout {
    String log001 = "UserLogoutImpl happened:";

    @SneakyThrows
    @Override
    public ResponseJson<UserLogoutOutVo> doService(UserLogoutInVo inVo) {
        log.info("{}----------交易开始--------", log001);
        UserLogoutOutVo outVo = new UserLogoutOutVo();

        String userName = inVo.getUserName().trim();
        UserInfo userInfo = userInfoMapper.selectByUserName(userName);
        if (null == userInfo) {
            log.error("{}-------报错！没有这个人------", log);
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "无此用户");
        }

        List<BattleInfo> battleInfoList = battleInfoMapper.selectByRoomNumber(inVo.getRoomNumber());
        //开始插入房间
        BattleInfo battleInfo;
        if (battleInfoList.size() == 0) {
            //如果是新房间，则是房主
            log.error("{}-------报错！没有这个房间------", log);
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "无此房间");

        } else if (battleInfoList.size() == 1) {
            battleInfo = battleInfoList.get(0);
            List<UserInfo> players = objectMapper.readValue(battleInfo.getPlayers(), objectMapper.getTypeFactory().constructParametricType(List.class, UserInfo.class));
            for (int i = 0, playersSize = players.size(); i < playersSize; i++) {
                UserInfo player = players.get(i);
                if(player.getUserName().equals(userInfo.getUserName())){
                    userInfoMapper.clearRoomNumberByPrimaryKey(userInfo.getId());
                    players.remove(i);
                    break;
                }
            }
            //退到只剩一个人了，游戏结束
            if(players.size()==1){
                userInfoMapper.clearRoomNumberByPrimaryKey(players.get(0).getId());
                players.get(0).setHand(null);
                battleInfo.setStatus(OneCardConstant.Battle_Status_waiting);
            }

            if(players.isEmpty()){
                battleInfoMapper.deleteByPrimaryKey(battleInfo.getId());
            }else{
                battleInfo.setPlayers(objectMapper.writeValueAsString(players));
                battleInfoMapper.updateByPrimaryKey(battleInfo);
            }
        } else {
            log.error("{}--------房间{}不唯一！-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间冲突！请联系管理员");
        }

        return ResponseJson.ok();
    }

}

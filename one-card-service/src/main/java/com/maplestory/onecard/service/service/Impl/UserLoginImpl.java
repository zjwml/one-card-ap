package com.maplestory.onecard.service.service.Impl;

import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.mapper.BattleInfoMapper;
import com.maplestory.onecard.service.util.BeanUtils;
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
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserLoginImpl implements UserLogin {
    String log001 = "UserLoginImpl happened:";

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private BattleInfoMapper battleInfoMapper;

    @Override
    public ResponseJson<UserLoginOutVo> doService(UserLoginInVo inVo) {
        log.info("{}----------交易开始--------", log001);

        UserInfo userInfo = new UserInfo();
        try {
            log.info("{}--------开始查询是否存在这个用户-----", log001);
            userInfo = userInfoMapper.selectByUserName(inVo.getUserName());
            log.info("{}--------结束查询是否存在这个用户-----", log001);
        } catch (Exception e) {
            log.error("{}--------查询失败:{}-----", log001, e);
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "查询用户失败" + e.getMessage());
        }
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

        List<BattleInfo> battleInfoList = new ArrayList<>();
        try {
            log.info("{}--------开始查询是否存在这个房间-----", log001);
            battleInfoList = battleInfoMapper.selectByRoomNumber(inVo.getRoomNumber());
            log.info("{}--------结束查询是否存在这个房间-----", log001);
        } catch (Exception e) {
            log.error("{}--------查询失败:{}-----", log001, e);
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "查询用户失败" + e.getMessage());
        }
        //开始插入房间
        BattleInfo battleInfo = new BattleInfo();
        if (battleInfoList.size() == 0) {
            //如果是新房间，则是房主
            battleInfo = new BattleInfo();
            battleInfo.setPlayer1(userInfo.getId());
            battleInfo.setRoomNumber(inVo.getRoomNumber());
            try {
                log.info("{}--------开始新增这个房间{}-----", log001, inVo.getRoomNumber());
                int id = battleInfoMapper.insertSelective(battleInfo);
                log.info("{}--------结束新增这个房间{}-----", log001, inVo.getRoomNumber());
            } catch (Exception e) {
                log.error("{}--------新增失败:{}-----", log001, e);
                return ResponseJson.failure(OneCardConstant.Code_OtherFail, "新增房间失败" + e.getMessage());
            }
        } else if (battleInfoList.size() == 1) {
            battleInfo = battleInfoList.get(0);
            //判断情况
            int pos = getPosition(battleInfo, userInfo);

            if (5 == pos) {
                log.info("{}--------房间已满-----", log001);
                return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间已满");
            }
            if (0 == pos) {
                log.info("{}--------已在桌上-----", log001);
                UserLoginOutVo outVo = new UserLoginOutVo();
                outVo.setBattleInfo(BeanUtils.switchNullToEmpty(battleInfo));
                return ResponseJson.ok(outVo);
            }

            // 看看插入谁
            if (2 == pos) {
                battleInfo.setPlayer2(userInfo.getId());
            } else if (3 == pos) {
                battleInfo.setPlayer3(userInfo.getId());
            } else if (4 == pos) {
                battleInfo.setPlayer4(userInfo.getId());
            }

            try {
                log.info("{}--------开始更新这个房间{}-----", log001, inVo.getRoomNumber());
                int id = battleInfoMapper.updateByPrimaryKeySelective(battleInfo);
                log.info("{}--------结束更新这个房间{}-----", log001, inVo.getRoomNumber());
            } catch (Exception e) {
                log.error("{}--------更新失败:{}-----", log001, e);
                return ResponseJson.failure(OneCardConstant.Code_OtherFail, "更新房间失败" + e.getMessage());
            }
        } else {
            log.error("{}--------房间{}不唯一！-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间冲突！请联系管理员");
        }

        UserLoginOutVo outVo = new UserLoginOutVo();
        outVo.setBattleInfo(BeanUtils.switchNullToEmpty(battleInfo));
        return ResponseJson.ok(outVo);
    }

    /**
     * 判断可插位置
     *
     * @param battleInfo 战斗信息
     * @return 0-已入座，5-已满，其他是可以插入的位置
     */
    private int getPosition(BattleInfo battleInfo, UserInfo userInfo) {
        if (Objects.equals(battleInfo.getPlayer1(), userInfo.getId())) {
            return 0;
        }

        if (null == battleInfo.getPlayer2()) {
            return 2;
        } else if (Objects.equals(userInfo.getId(), battleInfo.getPlayer2())) {
            return 0;
        }

        if (null == battleInfo.getPlayer3()) {
            return 3;
        } else if (Objects.equals(userInfo.getId(), battleInfo.getPlayer3())) {
            return 0;
        }

        if (null == battleInfo.getPlayer4()) {
            return 4;
        } else if (Objects.equals(userInfo.getId(), battleInfo.getPlayer4())) {
            return 0;
        }

        return 5;
    }
}

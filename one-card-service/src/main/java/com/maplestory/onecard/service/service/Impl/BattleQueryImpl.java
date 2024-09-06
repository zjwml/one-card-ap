package com.maplestory.onecard.service.service.Impl;

import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.service.BattleQuery;
import com.maplestory.onecard.service.util.ListUtils;
import com.maplestory.onecard.service.vo.BattleInfoSubOutVo;
import com.maplestory.onecard.service.vo.BattleQueryInVo;
import com.maplestory.onecard.service.vo.BattleQueryOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BattleQueryImpl extends CommonService implements BattleQuery {

    private final String log001 = "BattleQueryImpl happened:";

    @Override
    public ResponseJson<BattleQueryOutVo> doService(BattleQueryInVo inVo) {
        log.info("{}----------交易开始--------", log001);

        UserInfo userInfo = userInfoMapper.selectByUserName(inVo.getUserName());
        if (null == userInfo) {
            log.error("{}--------用户不存在:{}-----", log001, inVo.getUserName());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "用户不存在");
        }

        List<BattleInfo> battleInfoList = battleInfoMapper.selectByRoomNumber(inVo.getRoomNumber());
        if (battleInfoList.isEmpty()) {
            log.error("{}--------房间不存在:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间不存在");
        }
        if (battleInfoList.size() > 1) {
            log.error("{}--------房间不唯一:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间异常不唯一，请联系管理员");
        }
        BattleInfo battleInfo = battleInfoList.get(0);
        BattleQueryOutVo outVo = new BattleQueryOutVo();
        outVo.setBattleInfoSubOutVo(getBattleInfoSubOutVo(battleInfo, userInfo));
        return ResponseJson.ok(outVo);
    }
}

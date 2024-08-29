package com.maplestory.onecard.service.service;

import com.maplestory.onecard.service.vo.BattleStartInVo;
import com.maplestory.onecard.service.vo.BattleStartOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;

public interface BattleStart {
    ResponseJson<BattleStartOutVo> doService(BattleStartInVo inVo);
}

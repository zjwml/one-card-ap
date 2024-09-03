package com.maplestory.onecard.service.service;

import com.maplestory.onecard.service.vo.BattleQueryInVo;
import com.maplestory.onecard.service.vo.BattleQueryOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;

public interface BattleQuery {
    ResponseJson<BattleQueryOutVo> doService(BattleQueryInVo inVo);
}

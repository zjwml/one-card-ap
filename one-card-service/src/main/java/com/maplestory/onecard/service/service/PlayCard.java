package com.maplestory.onecard.service.service;

import com.maplestory.onecard.service.vo.PlayCardInVo;
import com.maplestory.onecard.service.vo.PlayCardOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;

public interface PlayCard {
    ResponseJson<PlayCardOutVo> doService(PlayCardInVo inVo);
}

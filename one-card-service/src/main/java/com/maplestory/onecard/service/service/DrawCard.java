package com.maplestory.onecard.service.service;

import com.maplestory.onecard.service.vo.DrawCardInVo;
import com.maplestory.onecard.service.vo.DrawCardOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;

public interface DrawCard {
    ResponseJson<DrawCardOutVo> doService(DrawCardInVo inVo);
}

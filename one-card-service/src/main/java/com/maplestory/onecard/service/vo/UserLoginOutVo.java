package com.maplestory.onecard.service.vo;

import com.maplestory.onecard.model.domain.BattleInfo;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginOutVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private BattleInfo battleInfo;
}

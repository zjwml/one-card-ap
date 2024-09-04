package com.maplestory.onecard.service.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PlayCardOutVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    BattleInfoSubOutVo battleInfoSubOutVo;
}

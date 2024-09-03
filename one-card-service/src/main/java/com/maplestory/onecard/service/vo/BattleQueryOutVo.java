package com.maplestory.onecard.service.vo;

import com.maplestory.onecard.model.domain.UserInfo;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class BattleQueryOutVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    BattleInfoSubOutVo battleInfoSubOutVo;
}

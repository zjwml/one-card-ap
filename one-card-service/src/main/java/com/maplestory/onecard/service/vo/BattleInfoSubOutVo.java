package com.maplestory.onecard.service.vo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maplestory.onecard.model.domain.CardInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class BattleInfoSubOutVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 回合
     */
    private Long turn;

    /**
     * 攻击点数
     */
    private Integer attackLevel;

    /**
     * 顺序
     */
    private Integer direction;

    /**
     * 出牌者
     */
    private Long playPlayer;

    /**
     * 当前出的牌
     */
    private CardInfo playCard;

    /**
     * 房间号
     */
    private String roomNumber;

    /**
     * 00-未开始，01-进行中，02-已结束
     */
    private String status;

    /**
     * 用户ID集合
     */
    private List<UserInfo> players;

    /**
     * 手牌集合
     */
    private ObjectNode hands;
}

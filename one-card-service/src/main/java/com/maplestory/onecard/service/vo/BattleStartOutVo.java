package com.maplestory.onecard.service.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BattleStartOutVo implements Serializable {

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
     * 玩家1
     */
    private Long player1;

    /**
     * 玩家2
     */
    private Long player2;

    /**
     * 玩家3
     */
    private Long player3;

    /**
     * 玩家4
     */
    private Long player4;

    /**
     * 一号手牌
     */
    private String hand1;

    /**
     * 二号手牌
     */
    private String hand2;

    /**
     * 三号手牌
     */
    private String hand3;

    /**
     * 四号手牌
     */
    private String hand4;

    /**
     * 攻击点数
     */
    private Integer attackLevel;

    /**
     * 上一轮的卡
     */
    private String cardPrevious;

    /**
     * 顺序
     */
    private Integer direction;

    /**
     * 牌堆
     */
    private String deck;

    /**
     * 出牌者
     */
    private Long playPlayer;

    /**
     * 下一个玩家
     */
    private Long nextPlayer;

    /**
     * 当前出的牌
     */
    private String cardPlay;

    /**
     * 房间号
     */
    private String roomNumber;

    /**
     * 状态
     */
    private String status;
}

package com.maplestory.onecard.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 对战信息
 * @TableName battle_info
 */
@TableName(value ="battle_info")
@Data
public class BattleInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
    private Long cardPrevious;

    /**
     * 顺序
     */
    private Integer order;

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
    private Long cardPlay;

    /**
     * 房间号
     */
    private String number;

    /**
     * 状态
     */
    private String status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
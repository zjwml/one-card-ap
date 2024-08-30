package com.maplestory.onecard.model.domain;

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
     * 攻击点数
     */
    private Integer attackLevel;

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
     * 当前出的牌
     */
    private String playCard;

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
    private String players;

    /**
     * 手牌集合
     */
    private String hands;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
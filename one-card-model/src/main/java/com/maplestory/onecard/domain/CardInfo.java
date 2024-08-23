package com.maplestory.onecard.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 卡牌信息
 * @TableName card_info
 */
@TableName(value ="card_info")
@Data
public class CardInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 颜色
     */
    private String color;

    /**
     * 点数
     */
    private String point;

    /**
     * 描述
     */
    private String desc;

    /**
     * 图片路径
     */
    private String image;

    /**
     * 1-可进牌堆，0-不可进
     */
    private Integer available;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
package com.maplestory.onecard.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 用户信息表
 * @TableName user_info
 */
@TableName(value ="user_info")
@Data
public class UserInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    private String userName;

    /**
     * 用户名
     */
    @JsonIgnore
    private String userId;

    /**
     * 0-正常，1-其他
     */
    @JsonIgnore
    private String status;

    /**
     * 所在房间
     */
    @JsonIgnore
    private String roomNumber;

    /**
     * 辅助字段，手牌
     */
    private String hand;

    /**
     * 
     */
    @JsonIgnore
    private String ppp;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
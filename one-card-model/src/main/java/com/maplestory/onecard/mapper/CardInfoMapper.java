package com.maplestory.onecard.mapper;

import com.maplestory.onecard.domain.CardInfo;

import java.util.List;

/**
* @author wing
* @description 针对表【card_info(卡牌信息)】的数据库操作Mapper
* @createDate 2024-08-23 16:28:06
* @Entity com.maplestory.onecard.domain.CardInfo
*/
public interface CardInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(CardInfo record);

    int insertSelective(CardInfo record);

    CardInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CardInfo record);

    int updateByPrimaryKey(CardInfo record);

    List<CardInfo> selectAvailable();

}

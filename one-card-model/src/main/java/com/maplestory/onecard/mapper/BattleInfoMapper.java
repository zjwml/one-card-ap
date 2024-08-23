package com.maplestory.onecard.mapper;

import com.maplestory.onecard.domain.BattleInfo;

/**
* @author wing
* @description 针对表【battle_info(对战信息)】的数据库操作Mapper
* @createDate 2024-08-23 16:55:46
* @Entity com.maplestory.onecard.domain.BattleInfo
*/
public interface BattleInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(BattleInfo record);

    int insertSelective(BattleInfo record);

    BattleInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BattleInfo record);

    int updateByPrimaryKey(BattleInfo record);

}

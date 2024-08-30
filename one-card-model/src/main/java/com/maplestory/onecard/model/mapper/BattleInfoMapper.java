package com.maplestory.onecard.model.mapper;

import com.maplestory.onecard.model.domain.BattleInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author wing
* @description 针对表【battle_info(对战信息)】的数据库操作Mapper
* @createDate 2024-08-30 10:42:59
* @Entity com.maplestory.onecard.model.domain.BattleInfo
*/
@Mapper
public interface BattleInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(BattleInfo record);

    int insertSelective(BattleInfo record);

    BattleInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BattleInfo record);

    int updateByPrimaryKey(BattleInfo record);

    List<BattleInfo> selectByRoomNumber(String roomNumber);

}

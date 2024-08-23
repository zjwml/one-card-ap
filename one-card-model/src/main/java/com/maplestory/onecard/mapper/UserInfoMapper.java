package com.maplestory.onecard.mapper;

import com.maplestory.onecard.domain.UserInfo;

/**
* @author wing
* @description 针对表【user_info(用户信息表)】的数据库操作Mapper
* @createDate 2024-08-23 16:44:12
* @Entity com.maplestory.onecard.domain.UserInfo
*/
public interface UserInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByUserId(String userId);
}

package com.maplestory.onecard.model.mapper;

import com.maplestory.onecard.model.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author wing
* @description 针对表【user_info(用户信息表)】的数据库操作Mapper
* @createDate 2024-09-06 10:32:49
* @Entity com.maplestory.onecard.model.domain.UserInfo
*/
@Mapper
public interface UserInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByUserName(String userName);
}

package com.maplestory.onecard.model.mapper;

import com.maplestory.onecard.model.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author wing
* @description 针对表【user_info(用户信息表)】的数据库操作Mapper
* @createDate 2024-08-23 16:44:12
* @Entity com.maplestory.onecard.domain.UserInfo
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

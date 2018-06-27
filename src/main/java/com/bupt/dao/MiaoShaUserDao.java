package com.bupt.dao;

import com.bupt.domain.MiaoShaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 建立一个与MiaoShaUser对应的Dao
 * */
@Mapper
public interface MiaoShaUserDao {

    /**
     * @param id
     * @return
     */
    @Select("select * from miaosha_user where id = #{id}")
    public MiaoShaUser getById(@Param("id") long id);
}

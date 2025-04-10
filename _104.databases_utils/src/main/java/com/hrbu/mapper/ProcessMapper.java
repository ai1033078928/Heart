package com.hrbu.mapper;

import com.hrbu.entity.Process;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProcessMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Process record);

    int insertSelective(Process record);

    Process selectByPrimaryKey(Integer id);

    @Select("select * from process")
    List<Process> queryAllProcess();

    int updateByPrimaryKeySelective(Process record);

    int updateByPrimaryKey(Process record);
}
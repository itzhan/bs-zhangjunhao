package com.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sports.entity.Department;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}

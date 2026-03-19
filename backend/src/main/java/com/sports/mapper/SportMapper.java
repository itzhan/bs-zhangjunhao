package com.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sports.entity.Sport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SportMapper extends BaseMapper<Sport> {
}

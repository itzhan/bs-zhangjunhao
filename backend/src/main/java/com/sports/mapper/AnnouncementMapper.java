package com.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sports.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}

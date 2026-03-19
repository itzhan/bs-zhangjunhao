package com.sports.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.common.BusinessException;
import com.sports.entity.Announcement;
import com.sports.entity.SysUser;
import com.sports.mapper.AnnouncementMapper;
import com.sports.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final SysUserMapper userMapper;

    public Page<Announcement> listPublished(int page, int size, Integer type) {
        Page<Announcement> p = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1);
        if (type != null) wrapper.eq(Announcement::getType, type);
        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getPublishTime);
        Page<Announcement> result = announcementMapper.selectPage(p, wrapper);
        fillAuthorName(result.getRecords());
        return result;
    }

    public Announcement getById(Long id) {
        Announcement ann = announcementMapper.selectById(id);
        if (ann == null) throw new BusinessException(404, "公告不存在");
        // 增加浏览量
        ann.setViewCount(ann.getViewCount() + 1);
        announcementMapper.updateById(ann);
        if (ann.getAuthorId() != null) {
            SysUser author = userMapper.selectById(ann.getAuthorId());
            if (author != null) ann.setAuthorName(author.getRealName());
        }
        return ann;
    }

    public Page<Announcement> adminListAnnouncements(int page, int size, Integer type, Integer status) {
        Page<Announcement> p = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (type != null) wrapper.eq(Announcement::getType, type);
        if (status != null) wrapper.eq(Announcement::getStatus, status);
        wrapper.orderByDesc(Announcement::getCreatedAt);
        Page<Announcement> result = announcementMapper.selectPage(p, wrapper);
        fillAuthorName(result.getRecords());
        return result;
    }

    public void createAnnouncement(Announcement announcement) {
        if (announcement.getStatus() == 1 && announcement.getPublishTime() == null) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        if (announcement.getViewCount() == null) announcement.setViewCount(0);
        announcementMapper.insert(announcement);
    }

    public void updateAnnouncement(Long id, Announcement updateData) {
        Announcement ann = announcementMapper.selectById(id);
        if (ann == null) throw new BusinessException(404, "公告不存在");
        if (updateData.getTitle() != null) ann.setTitle(updateData.getTitle());
        if (updateData.getContent() != null) ann.setContent(updateData.getContent());
        if (updateData.getType() != null) ann.setType(updateData.getType());
        if (updateData.getIsTop() != null) ann.setIsTop(updateData.getIsTop());
        if (updateData.getStatus() != null) {
            ann.setStatus(updateData.getStatus());
            if (updateData.getStatus() == 1 && ann.getPublishTime() == null) {
                ann.setPublishTime(LocalDateTime.now());
            }
        }
        announcementMapper.updateById(ann);
    }

    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }

    private void fillAuthorName(List<Announcement> list) {
        list.forEach(a -> {
            if (a.getAuthorId() != null) {
                SysUser author = userMapper.selectById(a.getAuthorId());
                if (author != null) a.setAuthorName(author.getRealName());
            }
        });
    }
}

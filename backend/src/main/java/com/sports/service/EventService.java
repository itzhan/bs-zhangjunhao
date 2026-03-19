package com.sports.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.common.BusinessException;
import com.sports.entity.*;
import com.sports.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final SportMapper sportMapper;
    private final RegistrationMapper registrationMapper;

    // ========== 公开接口 ==========

    public List<Event> listAll() {
        return eventMapper.selectList(
                new LambdaQueryWrapper<Event>().orderByDesc(Event::getStartDate)
        );
    }

    public Event getById(Long id) {
        Event event = eventMapper.selectById(id);
        if (event == null) throw new BusinessException(404, "赛事不存在");
        return event;
    }

    public Event getCurrentEvent() {
        return eventMapper.selectOne(
                new LambdaQueryWrapper<Event>()
                        .in(Event::getStatus, 1, 2)
                        .orderByDesc(Event::getStartDate)
                        .last("LIMIT 1")
        );
    }

    // ========== 管理员接口 ==========

    public Page<Event> adminListEvents(int page, int size, String keyword, Integer status) {
        Page<Event> p = new Page<>(page, size);
        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Event::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Event::getStatus, status);
        }
        wrapper.orderByDesc(Event::getStartDate);
        return eventMapper.selectPage(p, wrapper);
    }

    public void createEvent(Event event) {
        eventMapper.insert(event);
    }

    public void updateEvent(Long id, Event updateData) {
        Event event = eventMapper.selectById(id);
        if (event == null) throw new BusinessException(404, "赛事不存在");
        if (updateData.getName() != null) event.setName(updateData.getName());
        if (updateData.getDescription() != null) event.setDescription(updateData.getDescription());
        if (updateData.getCoverImage() != null) event.setCoverImage(updateData.getCoverImage());
        if (updateData.getStartDate() != null) event.setStartDate(updateData.getStartDate());
        if (updateData.getEndDate() != null) event.setEndDate(updateData.getEndDate());
        if (updateData.getLocation() != null) event.setLocation(updateData.getLocation());
        if (updateData.getRegStartDate() != null) event.setRegStartDate(updateData.getRegStartDate());
        if (updateData.getRegEndDate() != null) event.setRegEndDate(updateData.getRegEndDate());
        if (updateData.getMaxParticipants() != null) event.setMaxParticipants(updateData.getMaxParticipants());
        if (updateData.getStatus() != null) event.setStatus(updateData.getStatus());
        eventMapper.updateById(event);
    }

    public void deleteEvent(Long id) {
        eventMapper.deleteById(id);
    }

    public Map<String, Object> getEventStats(Long eventId) {
        Map<String, Object> stats = new HashMap<>();
        Long sportCount = sportMapper.selectCount(
                new LambdaQueryWrapper<Sport>().eq(Sport::getEventId, eventId)
        );
        Long regCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>().eq(Registration::getEventId, eventId)
        );
        Long approvedCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getEventId, eventId)
                        .eq(Registration::getStatus, 1)
        );
        stats.put("sportCount", sportCount);
        stats.put("registrationCount", regCount);
        stats.put("approvedCount", approvedCount);
        return stats;
    }
}

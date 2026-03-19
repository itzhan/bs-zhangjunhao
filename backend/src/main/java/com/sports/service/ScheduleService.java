package com.sports.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.common.BusinessException;
import com.sports.entity.*;
import com.sports.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final SportMapper sportMapper;
    private final EventMapper eventMapper;
    private final RegistrationMapper registrationMapper;

    public List<Schedule> getMySchedules(Long userId) {
        // 获取用户已通过的报名项目
        List<Registration> regs = registrationMapper.selectList(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getUserId, userId)
                        .eq(Registration::getStatus, 1)
        );
        if (regs.isEmpty()) return List.of();

        List<Long> sportIds = regs.stream().map(Registration::getSportId).toList();
        List<Schedule> schedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<Schedule>()
                        .in(Schedule::getSportId, sportIds)
                        .orderByAsc(Schedule::getMatchDate)
                        .orderByAsc(Schedule::getStartTime)
        );
        fillScheduleInfo(schedules);
        return schedules;
    }

    public Page<Schedule> adminListSchedules(int page, int size, Long eventId, Long sportId, Integer status) {
        Page<Schedule> p = new Page<>(page, size);
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        if (eventId != null) wrapper.eq(Schedule::getEventId, eventId);
        if (sportId != null) wrapper.eq(Schedule::getSportId, sportId);
        if (status != null) wrapper.eq(Schedule::getStatus, status);
        wrapper.orderByAsc(Schedule::getMatchDate).orderByAsc(Schedule::getStartTime);
        Page<Schedule> result = scheduleMapper.selectPage(p, wrapper);
        fillScheduleInfo(result.getRecords());
        return result;
    }

    public void createSchedule(Schedule schedule) {
        scheduleMapper.insert(schedule);
    }

    public void updateSchedule(Long id, Schedule updateData) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) throw new BusinessException(404, "赛程不存在");
        if (updateData.getSportId() != null) schedule.setSportId(updateData.getSportId());
        if (updateData.getEventId() != null) schedule.setEventId(updateData.getEventId());
        if (updateData.getRoundName() != null) schedule.setRoundName(updateData.getRoundName());
        if (updateData.getMatchDate() != null) schedule.setMatchDate(updateData.getMatchDate());
        if (updateData.getStartTime() != null) schedule.setStartTime(updateData.getStartTime());
        if (updateData.getEndTime() != null) schedule.setEndTime(updateData.getEndTime());
        if (updateData.getVenue() != null) schedule.setVenue(updateData.getVenue());
        if (updateData.getGroupNo() != null) schedule.setGroupNo(updateData.getGroupNo());
        if (updateData.getDescription() != null) schedule.setDescription(updateData.getDescription());
        if (updateData.getStatus() != null) schedule.setStatus(updateData.getStatus());
        scheduleMapper.updateById(schedule);
    }

    public void deleteSchedule(Long id) {
        scheduleMapper.deleteById(id);
    }

    private void fillScheduleInfo(List<Schedule> list) {
        list.forEach(s -> {
            Sport sport = sportMapper.selectById(s.getSportId());
            if (sport != null) s.setSportName(sport.getName());
            Event event = eventMapper.selectById(s.getEventId());
            if (event != null) s.setEventName(event.getName());
        });
    }
}

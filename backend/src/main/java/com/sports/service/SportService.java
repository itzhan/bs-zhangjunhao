package com.sports.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.common.BusinessException;
import com.sports.entity.Sport;
import com.sports.entity.Registration;
import com.sports.mapper.EventMapper;
import com.sports.mapper.RegistrationMapper;
import com.sports.mapper.SportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SportService {

    private final SportMapper sportMapper;
    private final EventMapper eventMapper;
    private final RegistrationMapper registrationMapper;

    public List<Sport> listByEvent(Long eventId) {
        List<Sport> sports = sportMapper.selectList(
                new LambdaQueryWrapper<Sport>().eq(Sport::getEventId, eventId)
                        .eq(Sport::getStatus, 1)
                        .orderByAsc(Sport::getCategory)
        );
        sports.forEach(s -> {
            Long count = registrationMapper.selectCount(
                    new LambdaQueryWrapper<Registration>()
                            .eq(Registration::getSportId, s.getId())
                            .eq(Registration::getStatus, 1)
            );
            s.setRegistrationCount(count.intValue());
        });
        return sports;
    }

    public Sport getById(Long id) {
        Sport sport = sportMapper.selectById(id);
        if (sport == null) throw new BusinessException(404, "项目不存在");
        var event = eventMapper.selectById(sport.getEventId());
        if (event != null) sport.setEventName(event.getName());
        return sport;
    }

    public Page<Sport> adminListSports(int page, int size, Long eventId, String keyword, String category) {
        Page<Sport> p = new Page<>(page, size);
        LambdaQueryWrapper<Sport> wrapper = new LambdaQueryWrapper<>();
        if (eventId != null) wrapper.eq(Sport::getEventId, eventId);
        if (keyword != null && !keyword.isEmpty()) wrapper.like(Sport::getName, keyword);
        if (category != null && !category.isEmpty()) wrapper.eq(Sport::getCategory, category);
        wrapper.orderByAsc(Sport::getCategory).orderByAsc(Sport::getId);
        Page<Sport> result = sportMapper.selectPage(p, wrapper);
        result.getRecords().forEach(s -> {
            var event = eventMapper.selectById(s.getEventId());
            if (event != null) s.setEventName(event.getName());
        });
        return result;
    }

    public void createSport(Sport sport) {
        sportMapper.insert(sport);
    }

    public void updateSport(Long id, Sport updateData) {
        Sport sport = sportMapper.selectById(id);
        if (sport == null) throw new BusinessException(404, "项目不存在");
        if (updateData.getName() != null) sport.setName(updateData.getName());
        if (updateData.getCategory() != null) sport.setCategory(updateData.getCategory());
        if (updateData.getGenderLimit() != null) sport.setGenderLimit(updateData.getGenderLimit());
        if (updateData.getMaxPerPerson() != null) sport.setMaxPerPerson(updateData.getMaxPerPerson());
        if (updateData.getMaxPerDept() != null) sport.setMaxPerDept(updateData.getMaxPerDept());
        if (updateData.getMaxParticipants() != null) sport.setMaxParticipants(updateData.getMaxParticipants());
        if (updateData.getUnit() != null) sport.setUnit(updateData.getUnit());
        if (updateData.getSortType() != null) sport.setSortType(updateData.getSortType());
        if (updateData.getDescription() != null) sport.setDescription(updateData.getDescription());
        if (updateData.getStatus() != null) sport.setStatus(updateData.getStatus());
        if (updateData.getEventId() != null) sport.setEventId(updateData.getEventId());
        sportMapper.updateById(sport);
    }

    public void deleteSport(Long id) {
        sportMapper.deleteById(id);
    }
}

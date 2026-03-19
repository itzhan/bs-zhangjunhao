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
public class ResultService {

    private final SportResultMapper resultMapper;
    private final SysUserMapper userMapper;
    private final SportMapper sportMapper;
    private final EventMapper eventMapper;
    private final DepartmentMapper departmentMapper;

    public List<SportResult> getMyResults(Long userId) {
        List<SportResult> list = resultMapper.selectList(
                new LambdaQueryWrapper<SportResult>()
                        .eq(SportResult::getUserId, userId)
                        .orderByDesc(SportResult::getCreatedAt)
        );
        fillResultInfo(list);
        return list;
    }

    public List<Map<String, Object>> getRanking(Long eventId, Long sportId) {
        LambdaQueryWrapper<SportResult> wrapper = new LambdaQueryWrapper<>();
        if (eventId != null) wrapper.eq(SportResult::getEventId, eventId);
        if (sportId != null) wrapper.eq(SportResult::getSportId, sportId);
        wrapper.isNotNull(SportResult::getRanking).orderByAsc(SportResult::getRanking);

        List<SportResult> results = resultMapper.selectList(wrapper);
        fillResultInfo(results);

        return results.stream().map(r -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", r.getId());
            map.put("ranking", r.getRanking());
            map.put("userName", r.getUserName());
            map.put("departmentName", r.getDepartmentName());
            map.put("sportName", r.getSportName());
            map.put("score", r.getScore());
            map.put("isRecord", r.getIsRecord());
            return map;
        }).collect(Collectors.toList());
    }

    public Page<SportResult> adminListResults(int page, int size, Long eventId, Long sportId, String keyword) {
        Page<SportResult> p = new Page<>(page, size);
        LambdaQueryWrapper<SportResult> wrapper = new LambdaQueryWrapper<>();
        if (eventId != null) wrapper.eq(SportResult::getEventId, eventId);
        if (sportId != null) wrapper.eq(SportResult::getSportId, sportId);
        wrapper.orderByAsc(SportResult::getRanking);
        Page<SportResult> result = resultMapper.selectPage(p, wrapper);
        fillResultInfo(result.getRecords());
        return result;
    }

    public void createResult(SportResult sportResult) {
        resultMapper.insert(sportResult);
    }

    public void updateResult(Long id, SportResult updateData) {
        SportResult r = resultMapper.selectById(id);
        if (r == null) throw new BusinessException(404, "成绩记录不存在");
        if (updateData.getScore() != null) r.setScore(updateData.getScore());
        if (updateData.getScoreNumeric() != null) r.setScoreNumeric(updateData.getScoreNumeric());
        if (updateData.getRanking() != null) r.setRanking(updateData.getRanking());
        if (updateData.getIsRecord() != null) r.setIsRecord(updateData.getIsRecord());
        if (updateData.getRemark() != null) r.setRemark(updateData.getRemark());
        if (updateData.getUserId() != null) r.setUserId(updateData.getUserId());
        if (updateData.getSportId() != null) r.setSportId(updateData.getSportId());
        if (updateData.getEventId() != null) r.setEventId(updateData.getEventId());
        if (updateData.getScheduleId() != null) r.setScheduleId(updateData.getScheduleId());
        resultMapper.updateById(r);
    }

    public void deleteResult(Long id) {
        resultMapper.deleteById(id);
    }

    private void fillResultInfo(List<SportResult> list) {
        list.forEach(r -> {
            SysUser user = userMapper.selectById(r.getUserId());
            if (user != null) {
                r.setUserName(user.getRealName());
                if (user.getDepartmentId() != null) {
                    var dept = departmentMapper.selectById(user.getDepartmentId());
                    if (dept != null) r.setDepartmentName(dept.getName());
                }
            }
            Sport sport = sportMapper.selectById(r.getSportId());
            if (sport != null) r.setSportName(sport.getName());
            Event event = eventMapper.selectById(r.getEventId());
            if (event != null) r.setEventName(event.getName());
        });
    }
}

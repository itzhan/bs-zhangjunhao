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
public class AwardService {

    private final AwardMapper awardMapper;
    private final SysUserMapper userMapper;
    private final SportMapper sportMapper;
    private final EventMapper eventMapper;
    private final DepartmentMapper departmentMapper;

    public List<Award> listByEvent(Long eventId) {
        List<Award> list = awardMapper.selectList(
                new LambdaQueryWrapper<Award>().eq(Award::getEventId, eventId)
                        .orderByAsc(Award::getSportId)
        );
        fillAwardInfo(list);
        return list;
    }

    public List<Award> getMyAwards(Long userId) {
        List<Award> list = awardMapper.selectList(
                new LambdaQueryWrapper<Award>().eq(Award::getUserId, userId)
                        .orderByDesc(Award::getCreatedAt)
        );
        fillAwardInfo(list);
        return list;
    }

    public Page<Award> adminListAwards(int page, int size, Long eventId, String awardType, String keyword) {
        Page<Award> p = new Page<>(page, size);
        LambdaQueryWrapper<Award> wrapper = new LambdaQueryWrapper<>();
        if (eventId != null) wrapper.eq(Award::getEventId, eventId);
        if (awardType != null && !awardType.isEmpty()) wrapper.eq(Award::getAwardType, awardType);
        wrapper.orderByAsc(Award::getSportId);
        Page<Award> result = awardMapper.selectPage(p, wrapper);
        fillAwardInfo(result.getRecords());
        return result;
    }

    public void createAward(Award award) {
        awardMapper.insert(award);
    }

    public void updateAward(Long id, Award updateData) {
        Award award = awardMapper.selectById(id);
        if (award == null) throw new BusinessException(404, "奖项不存在");
        if (updateData.getUserId() != null) award.setUserId(updateData.getUserId());
        if (updateData.getSportId() != null) award.setSportId(updateData.getSportId());
        if (updateData.getEventId() != null) award.setEventId(updateData.getEventId());
        if (updateData.getAwardType() != null) award.setAwardType(updateData.getAwardType());
        if (updateData.getAwardName() != null) award.setAwardName(updateData.getAwardName());
        if (updateData.getCertificateNo() != null) award.setCertificateNo(updateData.getCertificateNo());
        if (updateData.getRemark() != null) award.setRemark(updateData.getRemark());
        awardMapper.updateById(award);
    }

    public void deleteAward(Long id) {
        awardMapper.deleteById(id);
    }

    private void fillAwardInfo(List<Award> list) {
        list.forEach(a -> {
            SysUser user = userMapper.selectById(a.getUserId());
            if (user != null) {
                a.setUserName(user.getRealName());
                if (user.getDepartmentId() != null) {
                    var dept = departmentMapper.selectById(user.getDepartmentId());
                    if (dept != null) a.setDepartmentName(dept.getName());
                }
            }
            Sport sport = sportMapper.selectById(a.getSportId());
            if (sport != null) a.setSportName(sport.getName());
            Event event = eventMapper.selectById(a.getEventId());
            if (event != null) a.setEventName(event.getName());
        });
    }
}

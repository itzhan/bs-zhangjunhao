package com.sports.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.common.BusinessException;
import com.sports.dto.RegistrationDTO;
import com.sports.entity.*;
import com.sports.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final SportMapper sportMapper;
    private final EventMapper eventMapper;
    private final SysUserMapper userMapper;
    private final DepartmentMapper departmentMapper;

    public void register(Long userId, RegistrationDTO dto) {
        // 检查是否已报名
        Long count = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getUserId, userId)
                        .eq(Registration::getSportId, dto.getSportId())
                        .ne(Registration::getStatus, 3) // 排除已取消
        );
        if (count > 0) throw new BusinessException(400, "已报名该项目");

        // 检查赛事报名状态
        Event event = eventMapper.selectById(dto.getEventId());
        if (event == null) throw new BusinessException(404, "赛事不存在");
        if (event.getStatus() != 1) throw new BusinessException(400, "赛事未在报名中");

        // 检查项目
        Sport sport = sportMapper.selectById(dto.getSportId());
        if (sport == null) throw new BusinessException(404, "项目不存在");

        Registration reg = new Registration();
        reg.setUserId(userId);
        reg.setSportId(dto.getSportId());
        reg.setEventId(dto.getEventId());
        reg.setStatus(0); // 待审核
        reg.setRemark(dto.getRemark());
        registrationMapper.insert(reg);
    }

    public List<Registration> getMyRegistrations(Long userId) {
        List<Registration> list = registrationMapper.selectList(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getUserId, userId)
                        .orderByDesc(Registration::getCreatedAt)
        );
        fillRegistrationInfo(list);
        return list;
    }

    public void cancelRegistration(Long userId, Long id) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) throw new BusinessException(404, "报名记录不存在");
        if (!reg.getUserId().equals(userId)) throw new BusinessException(403, "不能取消他人报名");
        if (reg.getStatus() == 1) throw new BusinessException(400, "已审核通过，不能取消");
        reg.setStatus(3);
        registrationMapper.updateById(reg);
    }

    // ========== 管理员 ==========

    public Page<Registration> adminListRegistrations(int page, int size, Long eventId, Long sportId, Integer status, String keyword) {
        Page<Registration> p = new Page<>(page, size);
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        if (eventId != null) wrapper.eq(Registration::getEventId, eventId);
        if (sportId != null) wrapper.eq(Registration::getSportId, sportId);
        if (status != null) wrapper.eq(Registration::getStatus, status);
        wrapper.orderByDesc(Registration::getCreatedAt);
        Page<Registration> result = registrationMapper.selectPage(p, wrapper);
        fillRegistrationInfo(result.getRecords());

        if (keyword != null && !keyword.isEmpty()) {
            // 在内存中过滤（小规模数据可接受）
            result.getRecords().removeIf(r ->
                    !containsKeyword(r.getUserName(), keyword) &&
                    !containsKeyword(r.getStudentNo(), keyword)
            );
        }
        return result;
    }

    public void approveRegistration(Long id) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) throw new BusinessException(404, "报名记录不存在");
        reg.setStatus(1);
        registrationMapper.updateById(reg);
    }

    public void rejectRegistration(Long id, String reason) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) throw new BusinessException(404, "报名记录不存在");
        reg.setStatus(2);
        reg.setRejectReason(reason);
        registrationMapper.updateById(reg);
    }

    public void batchApprove(List<Long> ids) {
        ids.forEach(this::approveRegistration);
    }

    private void fillRegistrationInfo(List<Registration> list) {
        list.forEach(r -> {
            SysUser user = userMapper.selectById(r.getUserId());
            if (user != null) {
                r.setUserName(user.getRealName());
                r.setStudentNo(user.getStudentNo());
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

    private boolean containsKeyword(String value, String keyword) {
        return value != null && value.contains(keyword);
    }
}

package com.sports.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sports.entity.*;
import com.sports.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SysUserMapper userMapper;
    private final EventMapper eventMapper;
    private final SportMapper sportMapper;
    private final RegistrationMapper registrationMapper;
    private final SportResultMapper resultMapper;
    private final AwardMapper awardMapper;
    private final DepartmentMapper departmentMapper;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 基础统计
        data.put("userCount", userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "USER")
        ));
        data.put("eventCount", eventMapper.selectCount(null));
        data.put("sportCount", sportMapper.selectCount(null));
        data.put("registrationCount", registrationMapper.selectCount(null));
        data.put("pendingCount", registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>().eq(Registration::getStatus, 0)
        ));
        data.put("resultCount", resultMapper.selectCount(null));
        data.put("awardCount", awardMapper.selectCount(null));

        // 奖牌分布
        List<Award> allAwards = awardMapper.selectList(null);
        Map<String, Long> medalDistribution = allAwards.stream()
                .collect(Collectors.groupingBy(Award::getAwardType, Collectors.counting()));
        data.put("medalDistribution", medalDistribution);

        // 各院系参赛人数统计
        List<Department> departments = departmentMapper.selectList(null);
        List<Map<String, Object>> deptStats = new ArrayList<>();
        for (Department dept : departments) {
            List<SysUser> deptUsers = userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getDepartmentId, dept.getId())
            );
            List<Long> userIds = deptUsers.stream().map(SysUser::getId).toList();
            long regCount = 0;
            long awardCount = 0;
            if (!userIds.isEmpty()) {
                regCount = registrationMapper.selectCount(
                        new LambdaQueryWrapper<Registration>()
                                .in(Registration::getUserId, userIds)
                                .eq(Registration::getStatus, 1)
                );
                awardCount = awardMapper.selectCount(
                        new LambdaQueryWrapper<Award>().in(Award::getUserId, userIds)
                );
            }
            Map<String, Object> stat = new LinkedHashMap<>();
            stat.put("departmentName", dept.getName());
            stat.put("userCount", deptUsers.size());
            stat.put("registrationCount", regCount);
            stat.put("awardCount", awardCount);
            deptStats.add(stat);
        }
        data.put("departmentStats", deptStats);

        // 各赛事报名趋势（按赛事分组统计报名数）
        List<Event> events = eventMapper.selectList(
                new LambdaQueryWrapper<Event>().orderByDesc(Event::getStartDate)
        );
        List<Map<String, Object>> eventStats = new ArrayList<>();
        for (Event event : events) {
            Map<String, Object> es = new LinkedHashMap<>();
            es.put("eventName", event.getName());
            es.put("registrationCount", registrationMapper.selectCount(
                    new LambdaQueryWrapper<Registration>().eq(Registration::getEventId, event.getId())
            ));
            es.put("approvedCount", registrationMapper.selectCount(
                    new LambdaQueryWrapper<Registration>()
                            .eq(Registration::getEventId, event.getId())
                            .eq(Registration::getStatus, 1)
            ));
            eventStats.add(es);
        }
        data.put("eventStats", eventStats);

        return data;
    }

    // 各院系奖牌榜
    public List<Map<String, Object>> getMedalTable(Long eventId) {
        List<Department> departments = departmentMapper.selectList(null);
        List<Map<String, Object>> table = new ArrayList<>();

        for (Department dept : departments) {
            List<SysUser> deptUsers = userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getDepartmentId, dept.getId())
            );
            if (deptUsers.isEmpty()) continue;

            List<Long> userIds = deptUsers.stream().map(SysUser::getId).toList();
            LambdaQueryWrapper<Award> aw = new LambdaQueryWrapper<Award>().in(Award::getUserId, userIds);
            if (eventId != null) aw.eq(Award::getEventId, eventId);
            List<Award> awards = awardMapper.selectList(aw);

            long gold = awards.stream().filter(a -> "GOLD".equals(a.getAwardType())).count();
            long silver = awards.stream().filter(a -> "SILVER".equals(a.getAwardType())).count();
            long bronze = awards.stream().filter(a -> "BRONZE".equals(a.getAwardType())).count();

            if (gold + silver + bronze > 0) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("departmentName", dept.getName());
                row.put("gold", gold);
                row.put("silver", silver);
                row.put("bronze", bronze);
                row.put("total", gold + silver + bronze);
                table.add(row);
            }
        }

        // 按总奖牌数、金牌、银牌、铜牌排序
        table.sort((a, b) -> {
            int cmp = Long.compare((long) b.get("total"), (long) a.get("total"));
            if (cmp != 0) return cmp;
            cmp = Long.compare((long) b.get("gold"), (long) a.get("gold"));
            if (cmp != 0) return cmp;
            return Long.compare((long) b.get("silver"), (long) a.get("silver"));
        });

        return table;
    }
}

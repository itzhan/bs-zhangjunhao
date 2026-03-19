package com.sports.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.common.Result;
import com.sports.entity.*;
import com.sports.mapper.DepartmentMapper;
import com.sports.mapper.SystemConfigMapper;
import com.sports.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final EventService eventService;
    private final SportService sportService;
    private final RegistrationService registrationService;
    private final ScheduleService scheduleService;
    private final ResultService resultService;
    private final AwardService awardService;
    private final AnnouncementService announcementService;
    private final DashboardService dashboardService;
    private final DepartmentMapper departmentMapper;
    private final SystemConfigMapper systemConfigMapper;

    // ===== 仪表盘 =====
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(dashboardService.getDashboardData());
    }

    @GetMapping("/statistics/medal-table")
    public Result<List<Map<String, Object>>> medalTable(@RequestParam(required = false) Long eventId) {
        return Result.success(dashboardService.getMedalTable(eventId));
    }

    // ===== 用户管理 =====
    @GetMapping("/users")
    public Result<Page<SysUser>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        return Result.success(userService.adminListUsers(page, size, keyword, role));
    }

    @PutMapping("/users/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        userService.adminUpdateUser(id, user);
        return Result.success();
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.adminDeleteUser(id);
        return Result.success();
    }

    // ===== 赛事管理 =====
    @GetMapping("/events")
    public Result<Page<Event>> listEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(eventService.adminListEvents(page, size, keyword, status));
    }

    @PostMapping("/events")
    public Result<Void> createEvent(@RequestBody Event event) {
        eventService.createEvent(event);
        return Result.success();
    }

    @PutMapping("/events/{id}")
    public Result<Void> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        eventService.updateEvent(id, event);
        return Result.success();
    }

    @DeleteMapping("/events/{id}")
    public Result<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return Result.success();
    }

    // ===== 运动项目管理 =====
    @GetMapping("/sports")
    public Result<Page<Sport>> listSports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.success(sportService.adminListSports(page, size, eventId, keyword, category));
    }

    @PostMapping("/sports")
    public Result<Void> createSport(@RequestBody Sport sport) {
        sportService.createSport(sport);
        return Result.success();
    }

    @PutMapping("/sports/{id}")
    public Result<Void> updateSport(@PathVariable Long id, @RequestBody Sport sport) {
        sportService.updateSport(id, sport);
        return Result.success();
    }

    @DeleteMapping("/sports/{id}")
    public Result<Void> deleteSport(@PathVariable Long id) {
        sportService.deleteSport(id);
        return Result.success();
    }

    // ===== 报名管理 =====
    @GetMapping("/registrations")
    public Result<Page<Registration>> listRegistrations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long sportId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(registrationService.adminListRegistrations(page, size, eventId, sportId, status, keyword));
    }

    @PutMapping("/registrations/{id}/approve")
    public Result<Void> approveRegistration(@PathVariable Long id) {
        registrationService.approveRegistration(id);
        return Result.success();
    }

    @PutMapping("/registrations/{id}/reject")
    public Result<Void> rejectRegistration(@PathVariable Long id, @RequestBody Map<String, String> body) {
        registrationService.rejectRegistration(id, body.get("reason"));
        return Result.success();
    }

    @PutMapping("/registrations/batch-approve")
    public Result<Void> batchApprove(@RequestBody Map<String, List<Long>> body) {
        registrationService.batchApprove(body.get("ids"));
        return Result.success();
    }

    // ===== 赛程管理 =====
    @GetMapping("/schedules")
    public Result<Page<Schedule>> listSchedules(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long sportId,
            @RequestParam(required = false) Integer status) {
        return Result.success(scheduleService.adminListSchedules(page, size, eventId, sportId, status));
    }

    @PostMapping("/schedules")
    public Result<Void> createSchedule(@RequestBody Schedule schedule) {
        scheduleService.createSchedule(schedule);
        return Result.success();
    }

    @PutMapping("/schedules/{id}")
    public Result<Void> updateSchedule(@PathVariable Long id, @RequestBody Schedule schedule) {
        scheduleService.updateSchedule(id, schedule);
        return Result.success();
    }

    @DeleteMapping("/schedules/{id}")
    public Result<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return Result.success();
    }

    // ===== 成绩管理 =====
    @GetMapping("/results")
    public Result<Page<SportResult>> listResults(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long sportId,
            @RequestParam(required = false) String keyword) {
        return Result.success(resultService.adminListResults(page, size, eventId, sportId, keyword));
    }

    @PostMapping("/results")
    public Result<Void> createResult(@RequestBody SportResult sportResult) {
        resultService.createResult(sportResult);
        return Result.success();
    }

    @PutMapping("/results/{id}")
    public Result<Void> updateResult(@PathVariable Long id, @RequestBody SportResult sportResult) {
        resultService.updateResult(id, sportResult);
        return Result.success();
    }

    @DeleteMapping("/results/{id}")
    public Result<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        return Result.success();
    }

    // ===== 奖项管理 =====
    @GetMapping("/awards")
    public Result<Page<Award>> listAwards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) String awardType,
            @RequestParam(required = false) String keyword) {
        return Result.success(awardService.adminListAwards(page, size, eventId, awardType, keyword));
    }

    @PostMapping("/awards")
    public Result<Void> createAward(@RequestBody Award award) {
        awardService.createAward(award);
        return Result.success();
    }

    @PutMapping("/awards/{id}")
    public Result<Void> updateAward(@PathVariable Long id, @RequestBody Award award) {
        awardService.updateAward(id, award);
        return Result.success();
    }

    @DeleteMapping("/awards/{id}")
    public Result<Void> deleteAward(@PathVariable Long id) {
        awardService.deleteAward(id);
        return Result.success();
    }

    // ===== 公告管理 =====
    @GetMapping("/announcements")
    public Result<Page<Announcement>> listAnnouncements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(announcementService.adminListAnnouncements(page, size, type, status));
    }

    @PostMapping("/announcements")
    public Result<Void> createAnnouncement(@AuthenticationPrincipal SysUser currentUser,
                                            @RequestBody Announcement announcement) {
        announcement.setAuthorId(currentUser.getId());
        announcementService.createAnnouncement(announcement);
        return Result.success();
    }

    @PutMapping("/announcements/{id}")
    public Result<Void> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcementService.updateAnnouncement(id, announcement);
        return Result.success();
    }

    @DeleteMapping("/announcements/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }

    // ===== 院系管理 =====
    @GetMapping("/departments")
    public Result<List<Department>> listDepartments() {
        return Result.success(departmentMapper.selectList(null));
    }

    @PostMapping("/departments")
    public Result<Void> createDepartment(@RequestBody Department department) {
        departmentMapper.insert(department);
        return Result.success();
    }

    @PutMapping("/departments/{id}")
    public Result<Void> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        departmentMapper.updateById(department);
        return Result.success();
    }

    @DeleteMapping("/departments/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        departmentMapper.deleteById(id);
        return Result.success();
    }

    // ===== 系统配置 =====
    @GetMapping("/settings")
    public Result<List<SystemConfig>> getSettings() {
        return Result.success(systemConfigMapper.selectList(null));
    }

    @PutMapping("/settings")
    public Result<Void> updateSettings(@RequestBody List<SystemConfig> configs) {
        configs.forEach(c -> systemConfigMapper.updateById(c));
        return Result.success();
    }
}

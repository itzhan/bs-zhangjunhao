package com.sports.controller;

import com.sports.common.Result;
import com.sports.entity.Schedule;
import com.sports.entity.SysUser;
import com.sports.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/my")
    public Result<List<Schedule>> getMySchedules(@AuthenticationPrincipal SysUser currentUser) {
        return Result.success(scheduleService.getMySchedules(currentUser.getId()));
    }
}

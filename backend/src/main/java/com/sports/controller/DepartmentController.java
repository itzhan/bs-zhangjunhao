package com.sports.controller;

import com.sports.common.Result;
import com.sports.entity.Department;
import com.sports.mapper.DepartmentMapper;
import com.sports.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentMapper departmentMapper;
    private final DashboardService dashboardService;

    @GetMapping
    public Result<List<Department>> listAll() {
        return Result.success(departmentMapper.selectList(null));
    }

    @GetMapping("/medal-table")
    public Result<List<Map<String, Object>>> getMedalTable(
            @RequestParam(required = false) Long eventId) {
        return Result.success(dashboardService.getMedalTable(eventId));
    }
}

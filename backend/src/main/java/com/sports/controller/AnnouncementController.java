package com.sports.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.common.Result;
import com.sports.entity.Announcement;
import com.sports.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public Result<Page<Announcement>> listPublished(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer type) {
        return Result.success(announcementService.listPublished(page, size, type));
    }

    @GetMapping("/{id}")
    public Result<Announcement> getById(@PathVariable Long id) {
        return Result.success(announcementService.getById(id));
    }
}

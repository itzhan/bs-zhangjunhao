package com.sports.controller;

import com.sports.common.Result;
import com.sports.entity.Sport;
import com.sports.service.SportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @GetMapping
    public Result<List<Sport>> listByEvent(@RequestParam Long eventId) {
        return Result.success(sportService.listByEvent(eventId));
    }

    @GetMapping("/{id}")
    public Result<Sport> getById(@PathVariable Long id) {
        return Result.success(sportService.getById(id));
    }
}

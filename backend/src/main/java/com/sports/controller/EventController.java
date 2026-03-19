package com.sports.controller;

import com.sports.common.Result;
import com.sports.entity.Event;
import com.sports.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public Result<List<Event>> listAll() {
        return Result.success(eventService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Event> getById(@PathVariable Long id) {
        return Result.success(eventService.getById(id));
    }

    @GetMapping("/current")
    public Result<Event> getCurrent() {
        return Result.success(eventService.getCurrentEvent());
    }
}

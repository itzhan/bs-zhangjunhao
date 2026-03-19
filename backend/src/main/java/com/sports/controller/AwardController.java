package com.sports.controller;

import com.sports.common.Result;
import com.sports.entity.Award;
import com.sports.entity.SysUser;
import com.sports.service.AwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/awards")
@RequiredArgsConstructor
public class AwardController {

    private final AwardService awardService;

    @GetMapping
    public Result<List<Award>> listByEvent(@RequestParam Long eventId) {
        return Result.success(awardService.listByEvent(eventId));
    }

    @GetMapping("/my")
    public Result<List<Award>> getMyAwards(@AuthenticationPrincipal SysUser currentUser) {
        return Result.success(awardService.getMyAwards(currentUser.getId()));
    }
}

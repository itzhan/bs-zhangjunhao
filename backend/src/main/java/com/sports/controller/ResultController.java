package com.sports.controller;

import com.sports.common.Result;
import com.sports.entity.SportResult;
import com.sports.entity.SysUser;
import com.sports.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @GetMapping("/my")
    public Result<List<SportResult>> getMyResults(@AuthenticationPrincipal SysUser currentUser) {
        return Result.success(resultService.getMyResults(currentUser.getId()));
    }

    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> getRanking(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long sportId) {
        return Result.success(resultService.getRanking(eventId, sportId));
    }
}

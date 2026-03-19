package com.sports.controller;

import com.sports.common.Result;
import com.sports.entity.SysUser;
import com.sports.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<SysUser> getProfile(@AuthenticationPrincipal SysUser currentUser) {
        return Result.success(userService.getProfile(currentUser.getId()));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@AuthenticationPrincipal SysUser currentUser,
                                       @RequestBody SysUser updateData) {
        userService.updateProfile(currentUser.getId(), updateData);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@AuthenticationPrincipal SysUser currentUser,
                                        @RequestBody Map<String, String> body) {
        userService.updatePassword(currentUser.getId(), body.get("oldPassword"), body.get("newPassword"));
        return Result.success();
    }
}

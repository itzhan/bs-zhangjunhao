package com.sports.controller;

import com.sports.common.Result;
import com.sports.dto.RegistrationDTO;
import com.sports.entity.Registration;
import com.sports.entity.SysUser;
import com.sports.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public Result<Void> register(@AuthenticationPrincipal SysUser currentUser,
                                  @Valid @RequestBody RegistrationDTO dto) {
        registrationService.register(currentUser.getId(), dto);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<Registration>> getMyRegistrations(@AuthenticationPrincipal SysUser currentUser) {
        return Result.success(registrationService.getMyRegistrations(currentUser.getId()));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelRegistration(@AuthenticationPrincipal SysUser currentUser,
                                            @PathVariable Long id) {
        registrationService.cancelRegistration(currentUser.getId(), id);
        return Result.success();
    }
}

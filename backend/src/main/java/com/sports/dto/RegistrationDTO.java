package com.sports.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationDTO {
    @NotNull(message = "运动项目不能为空")
    private Long sportId;

    @NotNull(message = "赛事不能为空")
    private Long eventId;

    private String remark;
}

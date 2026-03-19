package com.sports.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度3-50")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度6-50")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    private Integer gender;
    private String phone;
    private String email;
    private Long departmentId;
    private String className;
    private String studentNo;
}

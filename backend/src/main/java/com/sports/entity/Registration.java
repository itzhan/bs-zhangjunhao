package com.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("registration")
public class Registration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long sportId;
    private Long eventId;
    private Integer status;
    private String rejectReason;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private LocalDateTime deletedAt;

    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String sportName;
    @TableField(exist = false)
    private String eventName;
    @TableField(exist = false)
    private String departmentName;
    @TableField(exist = false)
    private String studentNo;
}

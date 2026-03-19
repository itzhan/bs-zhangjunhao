package com.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("result")
public class SportResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long sportId;
    private Long eventId;
    private Long scheduleId;
    private String score;
    private BigDecimal scoreNumeric;
    private Integer ranking;
    private Integer isRecord;
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
}

package com.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sport")
public class Sport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long eventId;
    private String name;
    private String category;
    private Integer genderLimit;
    private Integer maxPerPerson;
    private Integer maxPerDept;
    private Integer maxParticipants;
    private String unit;
    private Integer sortType;
    private String description;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private LocalDateTime deletedAt;

    @TableField(exist = false)
    private String eventName;
    @TableField(exist = false)
    private Integer registrationCount;
}

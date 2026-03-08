-- =============================================
-- 高校运动会管理系统 - 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS `sports_meeting` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `sports_meeting`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET CHARACTER SET utf8mb4;
SET collation_connection = utf8mb4_general_ci;

-- ----------------------------
-- 1. 用户表 sys_user
-- 关系：被 registration / score / award_record / appeal 等引用
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username`    VARCHAR(50)  NOT NULL                COMMENT '用户名/登录账号',
  `password`    VARCHAR(200) NOT NULL                COMMENT '密码(BCrypt加密)',
  `real_name`   VARCHAR(50)  DEFAULT NULL            COMMENT '真实姓名',
  `gender`      TINYINT      DEFAULT 0               COMMENT '性别: 0-男 1-女',
  `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
  `email`       VARCHAR(100) DEFAULT NULL            COMMENT '邮箱',
  `student_no`  VARCHAR(30)  DEFAULT NULL            COMMENT '学号',
  `college`     VARCHAR(100) DEFAULT NULL            COMMENT '学院',
  `major`       VARCHAR(100) DEFAULT NULL            COMMENT '专业',
  `class_name`  VARCHAR(50)  DEFAULT NULL            COMMENT '班级',
  `avatar`      VARCHAR(500) DEFAULT NULL            COMMENT '头像URL',
  `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN-管理员 USER-普通用户',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态: 0-禁用 1-正常',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at`  DATETIME     DEFAULT NULL            COMMENT '逻辑删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  KEY `idx_role_status` (`role`, `status`),
  KEY `idx_college` (`college`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ----------------------------
-- 2. 运动会/赛事表 sport_event
-- 关系：被 sport_project / schedule / registration / score / award / announcement / appeal 引用
-- ----------------------------
DROP TABLE IF EXISTS `sport_event`;
CREATE TABLE `sport_event` (
  `id`                      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`                    VARCHAR(200) NOT NULL                COMMENT '赛事名称',
  `description`             VARCHAR(2000) DEFAULT NULL           COMMENT '赛事描述',
  `location`                VARCHAR(200) DEFAULT NULL            COMMENT '赛事地点',
  `start_date`              DATE         NOT NULL                COMMENT '赛事开始日期',
  `end_date`                DATE         NOT NULL                COMMENT '赛事结束日期',
  `registration_start`      DATETIME     DEFAULT NULL            COMMENT '报名开始时间',
  `registration_end`        DATETIME     DEFAULT NULL            COMMENT '报名截止时间',
  `status`                  TINYINT      NOT NULL DEFAULT 0      COMMENT '状态: 0-草稿 1-报名中 2-报名截止 3-进行中 4-已结束',
  `max_projects_per_person` INT          DEFAULT 3               COMMENT '每人限报项目数',
  `created_by`              BIGINT       DEFAULT NULL            COMMENT '创建人ID (关联 sys_user.id)',
  `created_at`              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at`              DATETIME     DEFAULT NULL            COMMENT '逻辑删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_date` (`start_date`, `end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运动会/赛事表';

-- ----------------------------
-- 3. 比赛项目表 sport_project
-- 关系：event_id 关联 sport_event.id；被 schedule / registration / score / award / appeal 引用
-- ----------------------------
DROP TABLE IF EXISTS `sport_project`;
CREATE TABLE `sport_project` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id`         BIGINT       NOT NULL                COMMENT '关联赛事ID (关联 sport_event.id)',
  `name`             VARCHAR(100) NOT NULL                COMMENT '项目名称',
  `category`         VARCHAR(30)  NOT NULL                COMMENT '项目分类: 田赛/径赛/球类/其他',
  `gender_limit`     TINYINT      NOT NULL DEFAULT 0      COMMENT '性别限制: 0-不限 1-仅男 2-仅女',
  `max_participants` INT          DEFAULT NULL            COMMENT '最大参赛人数',
  `min_participants` INT          DEFAULT NULL            COMMENT '最少参赛人数',
  `venue`            VARCHAR(200) DEFAULT NULL            COMMENT '比赛场地',
  `score_type`       TINYINT      NOT NULL DEFAULT 0      COMMENT '成绩类型: 0-计时 1-计距 2-计分',
  `unit`             VARCHAR(20)  DEFAULT NULL            COMMENT '成绩单位: 秒/米/分',
  `sort_order`       INT          DEFAULT 0               COMMENT '排序权重',
  `is_team`          TINYINT      NOT NULL DEFAULT 0      COMMENT '是否团体项目: 0-个人 1-团体',
  `team_size`        INT          DEFAULT NULL            COMMENT '团体项目人数',
  `status`           TINYINT      NOT NULL DEFAULT 0      COMMENT '状态: 0-未开始 1-进行中 2-已结束',
  `description`      VARCHAR(1000) DEFAULT NULL           COMMENT '项目描述',
  `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at`       DATETIME     DEFAULT NULL            COMMENT '逻辑删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_id` (`event_id`),
  KEY `idx_event_category` (`event_id`, `category`),
  KEY `idx_event_status` (`event_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='比赛项目表';

-- ----------------------------
-- 4. 赛程表 schedule
-- 关系：event_id 关联 sport_event.id；project_id 关联 sport_project.id
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id`    BIGINT       NOT NULL                COMMENT '关联赛事ID (关联 sport_event.id)',
  `project_id`  BIGINT       NOT NULL                COMMENT '关联项目ID (关联 sport_project.id)',
  `round`       VARCHAR(30)  NOT NULL                COMMENT '轮次: 预赛/半决赛/决赛',
  `round_order` INT          NOT NULL DEFAULT 1      COMMENT '轮次序号',
  `start_time`  DATETIME     NOT NULL                COMMENT '开始时间',
  `end_time`    DATETIME     DEFAULT NULL            COMMENT '结束时间',
  `venue`       VARCHAR(200) DEFAULT NULL            COMMENT '比赛场地',
  `status`      TINYINT      NOT NULL DEFAULT 0      COMMENT '状态: 0-未开始 1-进行中 2-已结束 3-已取消',
  `remark`      VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_project` (`event_id`, `project_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_event_status` (`event_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='赛程表';

-- ----------------------------
-- 5. 报名表 registration
-- 关系：event_id -> sport_event.id; project_id -> sport_project.id; user_id -> sys_user.id; review_by -> sys_user.id
-- ----------------------------
DROP TABLE IF EXISTS `registration`;
CREATE TABLE `registration` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id`      BIGINT       NOT NULL                COMMENT '关联赛事ID (关联 sport_event.id)',
  `project_id`    BIGINT       NOT NULL                COMMENT '关联项目ID (关联 sport_project.id)',
  `user_id`       BIGINT       NOT NULL                COMMENT '报名用户ID (关联 sys_user.id)',
  `status`        TINYINT      NOT NULL DEFAULT 0      COMMENT '状态: 0-待审核 1-已通过 2-已拒绝 3-已取消',
  `reject_reason` VARCHAR(500) DEFAULT NULL            COMMENT '拒绝原因',
  `review_by`     BIGINT       DEFAULT NULL            COMMENT '审核人ID (关联 sys_user.id)',
  `review_at`     DATETIME     DEFAULT NULL            COMMENT '审核时间',
  `remark`        VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_project_user` (`event_id`, `project_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_event_status` (`event_id`, `status`),
  KEY `idx_project_status` (`project_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报名表';

-- ----------------------------
-- 6. 分组表 group_info
-- 关系：schedule_id -> schedule.id
-- ----------------------------
DROP TABLE IF EXISTS `group_info`;
CREATE TABLE `group_info` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `schedule_id` BIGINT       NOT NULL                COMMENT '关联赛程ID (关联 schedule.id)',
  `group_name`  VARCHAR(50)  NOT NULL                COMMENT '组名(如第1组)',
  `group_order` INT          NOT NULL DEFAULT 1      COMMENT '组序号',
  `status`      TINYINT      NOT NULL DEFAULT 0      COMMENT '状态: 0-未开始 1-进行中 2-已结束',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_schedule_order` (`schedule_id`, `group_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='分组表';

-- ----------------------------
-- 7. 分组成员/道次表 group_member
-- 关系：group_id -> group_info.id; user_id -> sys_user.id
-- ----------------------------
DROP TABLE IF EXISTS `group_member`;
CREATE TABLE `group_member` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id`   BIGINT      NOT NULL                COMMENT '关联分组ID (关联 group_info.id)',
  `user_id`    BIGINT      NOT NULL                COMMENT '用户ID (关联 sys_user.id)',
  `lane`       INT         DEFAULT NULL            COMMENT '道次号',
  `bib_number` VARCHAR(20) DEFAULT NULL            COMMENT '号码牌',
  `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_user` (`group_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_group_lane` (`group_id`, `lane`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='分组成员/道次表';

-- ----------------------------
-- 8. 成绩表 score
-- 关系：event_id -> sport_event.id; project_id -> sport_project.id;
--       schedule_id -> schedule.id; group_id -> group_info.id;
--       user_id -> sys_user.id; recorded_by -> sys_user.id
-- ----------------------------
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id`      BIGINT       NOT NULL                COMMENT '关联赛事ID (关联 sport_event.id)',
  `project_id`    BIGINT       NOT NULL                COMMENT '关联项目ID (关联 sport_project.id)',
  `schedule_id`   BIGINT       DEFAULT NULL            COMMENT '关联赛程ID (关联 schedule.id)',
  `group_id`      BIGINT       DEFAULT NULL            COMMENT '关联分组ID (关联 group_info.id)',
  `user_id`       BIGINT       NOT NULL                COMMENT '运动员ID (关联 sys_user.id)',
  `score_value`   VARCHAR(50)  DEFAULT NULL            COMMENT '成绩值(字符串, 如10.52)',
  `score_numeric` DECIMAL(10,3) DEFAULT NULL           COMMENT '数值型成绩(用于排序)',
  `ranking`       INT          DEFAULT NULL            COMMENT '名次',
  `is_valid`      TINYINT      NOT NULL DEFAULT 1      COMMENT '是否有效: 0-无效 1-有效',
  `remark`        VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `recorded_by`   BIGINT       DEFAULT NULL            COMMENT '记录人ID (关联 sys_user.id)',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_project` (`event_id`, `project_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_schedule_group` (`schedule_id`, `group_id`),
  KEY `idx_project_ranking` (`project_id`, `ranking`),
  KEY `idx_event_user` (`event_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成绩表';

-- ----------------------------
-- 9. 奖项设置表 award
-- 关系：event_id -> sport_event.id; project_id -> sport_project.id (可为NULL)
-- ----------------------------
DROP TABLE IF EXISTS `award`;
CREATE TABLE `award` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id`         BIGINT       NOT NULL                COMMENT '关联赛事ID (关联 sport_event.id)',
  `project_id`       BIGINT       DEFAULT NULL            COMMENT '关联项目ID (关联 sport_project.id, NULL表示赛事级奖项)',
  `name`             VARCHAR(100) NOT NULL                COMMENT '奖项名称(如金牌/银牌/铜牌)',
  `ranking_required` INT          DEFAULT 0               COMMENT '所需名次(0表示无名次要求)',
  `description`      VARCHAR(500) DEFAULT NULL            COMMENT '奖项描述',
  `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_id` (`event_id`),
  KEY `idx_event_project` (`event_id`, `project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='奖项设置表';

-- ----------------------------
-- 10. 获奖记录表 award_record
-- 关系：award_id -> award.id; event_id -> sport_event.id;
--       project_id -> sport_project.id; user_id -> sys_user.id;
--       issued_by -> sys_user.id
-- ----------------------------
DROP TABLE IF EXISTS `award_record`;
CREATE TABLE `award_record` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `award_id`       BIGINT       NOT NULL                COMMENT '关联奖项ID (关联 award.id)',
  `event_id`       BIGINT       NOT NULL                COMMENT '关联赛事ID (关联 sport_event.id)',
  `project_id`     BIGINT       DEFAULT NULL            COMMENT '关联项目ID (关联 sport_project.id)',
  `user_id`        BIGINT       NOT NULL                COMMENT '获奖用户ID (关联 sys_user.id)',
  `certificate_no` VARCHAR(50)  DEFAULT NULL            COMMENT '证书编号',
  `issued_by`      BIGINT       DEFAULT NULL            COMMENT '颁发人ID (关联 sys_user.id)',
  `issued_at`      DATETIME     DEFAULT NULL            COMMENT '颁发时间',
  `remark`         VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_award_id` (`award_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_event_project` (`event_id`, `project_id`),
  KEY `idx_certificate_no` (`certificate_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='获奖记录表';

-- ----------------------------
-- 11. 公告通知表 announcement
-- 关系：event_id -> sport_event.id (可为NULL); published_by -> sys_user.id
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id`     BIGINT        DEFAULT NULL            COMMENT '关联赛事ID (关联 sport_event.id, NULL表示系统公告)',
  `title`        VARCHAR(200)  NOT NULL                COMMENT '公告标题',
  `content`      TEXT          NOT NULL                COMMENT '公告内容',
  `type`         TINYINT       NOT NULL DEFAULT 0      COMMENT '类型: 0-系统公告 1-赛事公告 2-成绩公示 3-赛程变更',
  `is_top`       TINYINT       NOT NULL DEFAULT 0      COMMENT '是否置顶: 0-否 1-是',
  `status`       TINYINT       NOT NULL DEFAULT 0      COMMENT '状态: 0-草稿 1-已发布 2-已撤回',
  `published_by` BIGINT        DEFAULT NULL            COMMENT '发布人ID (关联 sys_user.id)',
  `published_at` DATETIME      DEFAULT NULL            COMMENT '发布时间',
  `created_at`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at`   DATETIME      DEFAULT NULL            COMMENT '逻辑删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_id` (`event_id`),
  KEY `idx_type_status` (`type`, `status`),
  KEY `idx_is_top` (`is_top`, `published_at`),
  KEY `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告通知表';

-- ----------------------------
-- 12. 成绩申诉表 appeal
-- 关系：event_id -> sport_event.id; project_id -> sport_project.id;
--       user_id -> sys_user.id; score_id -> score.id; handled_by -> sys_user.id
-- ----------------------------
DROP TABLE IF EXISTS `appeal`;
CREATE TABLE `appeal` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id`   BIGINT       NOT NULL                COMMENT '关联赛事ID (关联 sport_event.id)',
  `project_id` BIGINT       NOT NULL                COMMENT '关联项目ID (关联 sport_project.id)',
  `user_id`    BIGINT       NOT NULL                COMMENT '申诉用户ID (关联 sys_user.id)',
  `score_id`   BIGINT       NOT NULL                COMMENT '关联成绩ID (关联 score.id)',
  `reason`     TEXT         NOT NULL                COMMENT '申诉原因',
  `status`     TINYINT      NOT NULL DEFAULT 0      COMMENT '状态: 0-待处理 1-已受理 2-已通过 3-已驳回',
  `reply`      TEXT         DEFAULT NULL            COMMENT '处理回复',
  `handled_by` BIGINT       DEFAULT NULL            COMMENT '处理人ID (关联 sys_user.id)',
  `handled_at` DATETIME     DEFAULT NULL            COMMENT '处理时间',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_project` (`event_id`, `project_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_score_id` (`score_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成绩申诉表';

-- ----------------------------
-- 13. 系统配置表 sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key`   VARCHAR(100) NOT NULL                COMMENT '配置键',
  `config_value` VARCHAR(1000) DEFAULT NULL           COMMENT '配置值',
  `description`  VARCHAR(500) DEFAULT NULL            COMMENT '配置描述',
  `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统配置表';

SET FOREIGN_KEY_CHECKS = 1;

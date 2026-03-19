-- 高校运动会管理系统 - 表结构
DROP DATABASE IF EXISTS sports_meeting;
CREATE DATABASE sports_meeting DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE sports_meeting;

SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

-- ----------------------------
-- 院系表
-- ----------------------------
DROP TABLE IF EXISTS department;
CREATE TABLE department (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '院系名称',
  code VARCHAR(50) NOT NULL UNIQUE COMMENT '院系编码',
  contact_person VARCHAR(50) COMMENT '联系人',
  contact_phone VARCHAR(20) COMMENT '联系电话',
  sort_order INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL
) COMMENT '院系表';

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名/学号',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) NOT NULL COMMENT '姓名',
  gender TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  phone VARCHAR(20) COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(255) COMMENT '头像URL',
  role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色 USER-参赛者 ADMIN-管理员',
  department_id BIGINT COMMENT '所属院系',
  class_name VARCHAR(50) COMMENT '班级',
  student_no VARCHAR(50) COMMENT '学号',
  status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  INDEX idx_username (username),
  INDEX idx_department (department_id),
  INDEX idx_role (role)
) COMMENT '用户表';

-- ----------------------------
-- 赛事/运动会表
-- ----------------------------
DROP TABLE IF EXISTS event;
CREATE TABLE event (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL COMMENT '赛事名称',
  description TEXT COMMENT '赛事描述',
  cover_image VARCHAR(255) COMMENT '封面图片',
  start_date DATE NOT NULL COMMENT '开始日期',
  end_date DATE NOT NULL COMMENT '结束日期',
  location VARCHAR(200) COMMENT '举办地点',
  reg_start_date DATETIME COMMENT '报名开始时间',
  reg_end_date DATETIME COMMENT '报名截止时间',
  max_participants INT DEFAULT 0 COMMENT '最大参赛人数 0-不限',
  status TINYINT DEFAULT 0 COMMENT '状态 0-筹备中 1-报名中 2-进行中 3-已结束',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  INDEX idx_status (status),
  INDEX idx_start_date (start_date)
) COMMENT '赛事/运动会表';

-- ----------------------------
-- 运动项目表
-- ----------------------------
DROP TABLE IF EXISTS sport;
CREATE TABLE sport (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL COMMENT '所属赛事',
  name VARCHAR(100) NOT NULL COMMENT '项目名称',
  category VARCHAR(50) COMMENT '项目分类 田赛/径赛/球类/趣味',
  gender_limit TINYINT DEFAULT 0 COMMENT '性别限制 0-不限 1-仅男 2-仅女',
  max_per_person INT DEFAULT 1 COMMENT '每人最多报名数',
  max_per_dept INT DEFAULT 0 COMMENT '每院系最多参赛人数 0-不限',
  max_participants INT DEFAULT 0 COMMENT '项目最大参赛人数 0-不限',
  unit VARCHAR(20) COMMENT '成绩单位（秒/米/分等）',
  sort_type TINYINT DEFAULT 1 COMMENT '排序方式 1-升序(用时少优先) 2-降序(距离远优先)',
  description TEXT COMMENT '项目描述',
  status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  INDEX idx_event (event_id),
  INDEX idx_category (category)
) COMMENT '运动项目表';

-- ----------------------------
-- 报名表
-- ----------------------------
DROP TABLE IF EXISTS registration;
CREATE TABLE registration (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '报名用户',
  sport_id BIGINT NOT NULL COMMENT '报名项目',
  event_id BIGINT NOT NULL COMMENT '所属赛事',
  status TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-已通过 2-已拒绝 3-已取消',
  reject_reason VARCHAR(255) COMMENT '拒绝原因',
  remark VARCHAR(255) COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  UNIQUE INDEX uk_user_sport (user_id, sport_id),
  INDEX idx_event (event_id),
  INDEX idx_status (status)
) COMMENT '报名表';

-- ----------------------------
-- 赛程安排表
-- ----------------------------
DROP TABLE IF EXISTS schedule;
CREATE TABLE schedule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sport_id BIGINT NOT NULL COMMENT '运动项目',
  event_id BIGINT NOT NULL COMMENT '所属赛事',
  round_name VARCHAR(50) COMMENT '轮次名称（预赛/决赛）',
  match_date DATE NOT NULL COMMENT '比赛日期',
  start_time TIME COMMENT '开始时间',
  end_time TIME COMMENT '结束时间',
  venue VARCHAR(100) COMMENT '比赛场地',
  group_no INT DEFAULT 1 COMMENT '组号',
  description VARCHAR(255) COMMENT '描述',
  status TINYINT DEFAULT 0 COMMENT '状态 0-未开始 1-进行中 2-已结束',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  INDEX idx_sport (sport_id),
  INDEX idx_event (event_id),
  INDEX idx_match_date (match_date)
) COMMENT '赛程安排表';

-- ----------------------------
-- 成绩表
-- ----------------------------
DROP TABLE IF EXISTS result;
CREATE TABLE result (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '运动员',
  sport_id BIGINT NOT NULL COMMENT '运动项目',
  event_id BIGINT NOT NULL COMMENT '所属赛事',
  schedule_id BIGINT COMMENT '对应赛程',
  score VARCHAR(50) COMMENT '成绩值',
  score_numeric DECIMAL(10,3) COMMENT '成绩数值(用于排序)',
  ranking INT COMMENT '名次',
  is_record TINYINT DEFAULT 0 COMMENT '是否破纪录 0-否 1-是',
  remark VARCHAR(255) COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  INDEX idx_user (user_id),
  INDEX idx_sport (sport_id),
  INDEX idx_event (event_id),
  INDEX idx_ranking (ranking)
) COMMENT '成绩表';

-- ----------------------------
-- 奖项表
-- ----------------------------
DROP TABLE IF EXISTS award;
CREATE TABLE award (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '获奖用户',
  sport_id BIGINT NOT NULL COMMENT '运动项目',
  event_id BIGINT NOT NULL COMMENT '所属赛事',
  award_type VARCHAR(50) NOT NULL COMMENT '奖项类型 GOLD/SILVER/BRONZE/EXCELLENCE',
  award_name VARCHAR(100) NOT NULL COMMENT '奖项名称',
  certificate_no VARCHAR(50) COMMENT '证书编号',
  remark VARCHAR(255) COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  INDEX idx_user (user_id),
  INDEX idx_event (event_id),
  INDEX idx_award_type (award_type)
) COMMENT '奖项表';

-- ----------------------------
-- 公告通知表
-- ----------------------------
DROP TABLE IF EXISTS announcement;
CREATE TABLE announcement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL COMMENT '标题',
  content TEXT NOT NULL COMMENT '内容',
  type TINYINT DEFAULT 0 COMMENT '类型 0-通知 1-公告 2-规则',
  is_top TINYINT DEFAULT 0 COMMENT '是否置顶 0-否 1-是',
  publish_time DATETIME COMMENT '发布时间',
  author_id BIGINT COMMENT '发布人',
  status TINYINT DEFAULT 1 COMMENT '状态 0-草稿 1-已发布',
  view_count INT DEFAULT 0 COMMENT '浏览量',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  INDEX idx_type (type),
  INDEX idx_status (status),
  INDEX idx_publish_time (publish_time)
) COMMENT '公告通知表';

-- ----------------------------
-- 系统配置表
-- ----------------------------
DROP TABLE IF EXISTS system_config;
CREATE TABLE system_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
  config_value TEXT COMMENT '配置值',
  description VARCHAR(255) COMMENT '描述',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统配置表';

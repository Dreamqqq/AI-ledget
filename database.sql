-- AI记账本系统 - 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS jizhang DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE jizhang;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    phone VARCHAR(11) UNIQUE NOT NULL COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    name VARCHAR(50) COMMENT '姓名',
    age INT COMMENT '年龄',
    occupation VARCHAR(50) COMMENT '职业',
    gender ENUM('MALE', 'FEMALE') NOT NULL COMMENT '性别',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 账单表
CREATE TABLE IF NOT EXISTS transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type ENUM('INCOME', 'EXPENSE') NOT NULL COMMENT '类型：收入/支出',
    category VARCHAR(50) NOT NULL COMMENT '类目',
    amount DECIMAL(10, 2) NOT NULL COMMENT '金额',
    date DATE NOT NULL COMMENT '日期',
    remark VARCHAR(255) COMMENT '备注',
    image_url VARCHAR(500) COMMENT '图片URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_date (date),
    INDEX idx_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    operation_type ENUM('CREATE', 'UPDATE', 'DELETE') NOT NULL COMMENT '操作类型',
    entity_type ENUM('TRANSACTION', 'USER') NOT NULL COMMENT '实体类型',
    entity_id BIGINT NOT NULL COMMENT '实体ID',
    old_value TEXT COMMENT '旧值（JSON）',
    new_value TEXT COMMENT '新值（JSON）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

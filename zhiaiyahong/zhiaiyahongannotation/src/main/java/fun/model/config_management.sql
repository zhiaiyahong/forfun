create table config_management(
id bigint(20) primary key NOT NULL AUTO_INCREMENT,
config_key varchar(64) UNIQUE not null  comment '配置建',
config_value varchar(1024) not null comment '配置内容',
version bigint(20) not null default 0 comment '版本号为时间戳毫秒',
is_del int default 0 comment '0在用 1删除',
insert_time datetime default current_timestamp comment '插入时间',
update_time datetime on update current_timestamp comment '更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置管理表'
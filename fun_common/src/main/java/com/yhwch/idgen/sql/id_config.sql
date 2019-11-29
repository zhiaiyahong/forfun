create table id_config(
biz_name varchar(64) UNIQUE not null  comment '业务名称',
step int not null comment'增长步长',
max_id bigint not null comment '当前增长最大值',
version bigint(20) not null default 0 comment '版本号为时间戳毫秒',
del int default 0 comment '0在用 1删除',
insert_time datetime default current_timestamp comment '插入时间',
update_time datetime on update current_timestamp comment '更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发号器配置表';

insert into id_config
(biz_name,step,max_id,version)
values
('fun_test',10,0,1569396059343),
('step1',1,0,1569396059343)

/**
发号器步长最小设置为2，一次保证双buffer可以正常运行
 */
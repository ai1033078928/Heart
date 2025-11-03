-- create schema test;

create table user(
                     id long primary key ,
                     name varchar(200),
                     sex varchar(10),
                     age int,
                     email varchar(200)
);

insert into user(id, name, sex, age, email) VALUES
                                                (1,'name-1','男',26,'name-1@gmail.com'),
                                                (2,'name-2','男',26,'name-2@gmail.com')
;


/*insert into user(id, name, sex, age, email) VALUES
                                                (1001,'name-1001','女',27,'name-1001@gmail.com'),
                                                (1002,'name-1002','女',27,'name-1002@gmail.com')
;*/
select * from user;
-- drop table user;


drop table heart_job_info;
create table heart_job_info
(
    id int,
    file_head varchar(200),
    file_path  varchar(200),
    archive_path  varchar(200),
    load_server_name varchar(50),
    target_table_name varchar(200),
    time_interval int,   -- 时间间隔
    time_unit varchar(10),   -- 时间间隔的单位day,hour,min
    job_status varchar(10), -- 任务状态
    job_enable varchar(10), -- 任务启用状态
    start_load_time varchar(50),
    last_load_time varchar(50),
    last_file_name varchar(200)
);

select id,
       file_head,
       file_path,
       archive_path,
       load_server_name,
       target_table_name,
       time_interval,
       time_unit,
       job_status,
       job_enable,
       start_load_time,
       last_load_time,
       last_file_name
from heart_job_info
where load_server_name = 'testJob';

select * from heart_job_info;
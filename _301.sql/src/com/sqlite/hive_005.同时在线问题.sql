-- 现有各直播间的用户访问记录表（live_events）如下，表中每行数据表达的信息为，一个用户何时进入了一个直播间，又在何时离开了该直播间
-- user_id(用户id)	live_id(直播间id)	in_datetime(进入直播间的时间)	out_datetime(离开直播间的时间)
-- 100             1               	2021-12-1 19:30:00             	2021-12-1 19:53:00
-- 100             2               	2021-12-1 21:01:00             	2021-12-1 22:00:00
-- 101             1               	2021-12-1 19:05:00             	2021-12-1 20:55:00

-- 现要求统计各直播间最大同时在线人数，期望结果如下
-- live_id	max_user_count
-- 1       4
-- 2       3
-- 3       2

drop table if exists live_events;
create table if not exists live_events
(
    user_id      int comment '用户id',
    live_id      int comment '直播id',
    in_datetime  string comment '进入直播间时间',
    out_datetime string comment '离开直播间时间'
)
    /*comment '直播间访问记录'*/;

-- INSERT overwrite table
insert into live_events
VALUES (100, 1, '2021-12-01 19:00:00', '2021-12-01 19:28:00'),
       (100, 1, '2021-12-01 19:30:00', '2021-12-01 19:53:00'),
       (100, 2, '2021-12-01 21:01:00', '2021-12-01 22:00:00'),
       (101, 1, '2021-12-01 19:05:00', '2021-12-01 20:55:00'),
       (101, 2, '2021-12-01 21:05:00', '2021-12-01 21:58:00'),
       (102, 1, '2021-12-01 19:10:00', '2021-12-01 19:25:00'),
       (102, 2, '2021-12-01 19:55:00', '2021-12-01 21:00:00'),
       (102, 3, '2021-12-01 21:05:00', '2021-12-01 22:05:00'),
       (104, 1, '2021-12-01 19:00:00', '2021-12-01 20:59:00'),
       (104, 2, '2021-12-01 21:57:00', '2021-12-01 22:56:00'),
       (105, 2, '2021-12-01 19:10:00', '2021-12-01 19:18:00'),
       (106, 3, '2021-12-01 19:01:00', '2021-12-01 21:10:00')
;

select live_id, max(user_count) as max_user_count
from (
     select user_id
           , live_id
           , event_time
           , event_change
           -- 2. 窗口函数，截止当前数据，直播间累计人数
           , sum(event_change) over (partition by live_id order by event_time) as user_count
      from (
            -- 1.展开事件，进入+1，离开-1
            select user_id
                 , live_id
                 , in_datetime as event_time
                 , 1           as event_change
            from live_events
            union all
            select user_id
                 , live_id
                 , out_datetime as event_time
                 , -1           as event_change
            from live_events) t1) t2
group by live_id

-- 现有各用户的登录记录表（login_events）如下，表中每行数据表达的信息是一个用户何时登录了平台
-- user_id	login_datetime
-- 100     2021-12-01 19:00:00
-- 100     2021-12-01 19:30:00
-- 100     2021-12-02 21:01:00

-- 现要求统计各用户最长的连续登录天数，间断一天也算作连续，例如：一个用户在1,3,5,6登录，则视为连续6天登录。期望结果如下
-- user_id	max_day_count
-- 100     3
-- 101     6
-- 102     3
-- 104     3
-- 105     1


drop table if exists login_events;
create table if not exists login_events
(
    user_id        int comment '用户id',
    login_datetime string comment '登录时间'
)
/*comment '直播间访问记录'*/;

/*INSERT overwrite table*/
insert into login_events
VALUES (100, '2021-12-01 19:00:00'),
       (100, '2021-12-01 19:30:00'),
       (100, '2021-12-02 21:01:00'),
       (100, '2021-12-03 11:01:00'),
       (101, '2021-12-01 19:05:00'),
       (101, '2021-12-01 21:05:00'),
       (101, '2021-12-03 21:05:00'),
       (101, '2021-12-05 15:05:00'),
       (101, '2021-12-06 19:05:00'),
       (102, '2021-12-01 19:55:00'),
       (102, '2021-12-01 21:05:00'),
       (102, '2021-12-02 21:57:00'),
       (102, '2021-12-03 19:10:00'),
       (104, '2021-12-04 21:57:00'),
       (104, '2021-12-02 22:57:00'),
       (105, '2021-12-01 10:01:00'),
       (105, '2021-12-02 10:01:00'),
       (105, '2021-12-05 10:01:00'),
       (105, '2021-12-06 10:01:00'),
       (105, '2021-12-07 10:01:00')
;

-- 结果
-- 6. 按用户汇总，获取最大天数
select user_id, max(day_count) as max_day_count
from (select user_id
           -- 4. 按用户和组汇总，获取当前组的最大日期和最小日期
           , max(login_date)                                             as max_login_date
           , min(login_date)                                             as min_login_date
           , flag
           -- 5. 计算当前组最大日期和最小日期的差值
           , julianday(max(login_date)) - julianday(min(login_date)) + 1 as day_count
      from (select user_id
                 , login_date
                 , lag_date
                 -- 3. 若当前日期与上次登录日期差值大于2，则为新的一组；开窗后sum组获取当前组序号
                 , sum(case when julianday(login_date) - julianday(lag_date) > 2 then 1 else 0 end)
                       over (partition by user_id order by login_date) as flag
            from (select user_id
                       , login_date
                       -- 2. 获取上一次登录日期，获取不到置为1970-01-01
                       , lag(login_date, 1, '1970-01-01') over (partition by user_id order by login_date) as lag_date
                  from (
                              -- 1. 按日期汇总去重
                              select user_id
                                   , substr(login_datetime, 1, 10) as login_date
                              from login_events e
                              group by user_id, substr(login_datetime, 1, 10)
                       ) t1
                 ) t2
            ) t3
      group by user_id, flag
      ) t4
group by user_id
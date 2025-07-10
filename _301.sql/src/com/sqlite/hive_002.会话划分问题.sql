-- ================================== 从零开始 ================================
-- 现有页面浏览记录表（page_view_events）如下，表中有每个用户的每次页面访问记录
-- user_id		page_id		view_timestamp
-- 100		home		1659950435
-- 100		good_search		1659950446
-- 100		good_list		1659950457
-- 100		home		1659950541
-- 100		good_detail		1659950552
-- 100		cart		1659950563
-- 101		home		1659950435
-- 101		good_search		1659950446
-- 101		good_list		1659950457
-- 101		home		1659950541
-- 101		good_detail		1659950552
-- 101		cart		1659950563
-- 102		home		1659950435
-- 102		good_search		1659950446
-- 102		good_list		1659950457
-- 103		home		1659950541
-- 103		good_detail		1659950552
-- 103		cart		1659950563

-- 规定若同一用户的相邻两次访问记录时间间隔小于60s，则认为两次浏览记录属于同一会话。现有如下需求，为属于同一会话的访问记录增加一个相同的会话id字段，期望结果如下
-- user_id		page_id		view_timestamp		session_id
-- 100		home		1659950435		100-1
-- 100		good_search		1659950446		100-1
-- 100		good_list		1659950457		100-1
-- 100		home		1659950541		100-2
-- 100		good_detail		1659950552		100-2
-- 100		cart		1659950563		100-2
-- 101		home		1659950435		101-1
-- 101		good_search		1659950446		101-1
-- 101		good_list		1659950457		101-1
-- 101		home		1659950541		101-2
-- 101		good_detail		1659950552		101-2
-- 101		cart		1659950563		101-2
-- 102		home		1659950435		102-1
-- 102		good_search		1659950446		102-1
-- 102		good_list		1659950457		102-1
-- 103		home		1659950541		103-1
-- 103		good_detail		1659950552		103-1


drop table if exists page_view_events;
create table if not exists page_view_events
(
    user_id        int comment '用户id',
    page_id        string comment '页面id',
    view_timestamp bigint comment '访问时间戳'
)
-- comment '页面访问记录'
;

/*insert overwrite table*/
insert into page_view_events
values (100, 'home', 1659950435),
       (100, 'good_search', 1659950446),
       (100, 'good_list', 1659950457),
       (100, 'home', 1659950541),
       (100, 'good_detail', 1659950552),
       (100, 'cart', 1659950563),
       (101, 'home', 1659950435),
       (101, 'good_search', 1659950446),
       (101, 'good_list', 1659950457),
       (101, 'home', 1659950541),
       (101, 'good_detail', 1659950552),
       (101, 'cart', 1659950563),
       (102, 'home', 1659950435),
       (102, 'good_search', 1659950446),
       (102, 'good_list', 1659950457),
       (103, 'home', 1659950541),
       (103, 'good_detail', 1659950552),
       (103, 'cart', 1659950563)
;

-- 结果
select user_id
     , page_id
     , view_timestamp
     , session_start_point
     -- 拼接，使用sum+窗口函数计算当前是第几个组
     , user_id || '-' || sum(session_start_point) over (partition by user_id order by view_timestamp) as session_id
from (select user_id
           , page_id
           , view_timestamp
           -- 计算当前时间戳和上一次访问时间戳差值，若大于60s，则置为1，即新的一组起始点
           , case when view_timestamp - pre_timestamp >= 60 then 1 else 0 end as session_start_point
      from (select user_id
                 , page_id
                 , view_timestamp
                 -- 使用 lag 函数取当前用户上一次访问时间，获取不到置为 0
                 , lag(view_timestamp, 1, 0) over (partition by user_id order by view_timestamp) as pre_timestamp
            from page_view_events) t1) t2

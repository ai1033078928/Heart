-- 现有各品牌优惠周期表（promotion_info）如下，其记录了每个品牌的每个优惠活动的周期，其中同一品牌的不同优惠活动的周期可能会有交叉
-- promotion_id	brand	start_date	end_date
-- 1	oppo	2021-06-05	2021-06-09
-- 2	oppo	2021-06-11	2021-06-21
-- 3	vivo	2021-06-05	2021-06-15

-- 现要求统计每个品牌的优惠总天数，若某个品牌在同一天有多个优惠活动，则只按一天计算。期望结果如下
-- brand	promotion_day_count
-- vivo	    17
-- oppo	    16
-- redmi	22
-- huawei	22

drop table if exists promotion_info;
create table promotion_info
(
    promotion_id string comment '优惠活动id',
    brand        string comment '优惠品牌',
    start_date   string comment '优惠活动开始日期',
    end_date     string comment '优惠活动结束日期'
) /*comment '各品牌活动周期表'*/;

-- insert overwrite table
insert into promotion_info
values (1, 'oppo', '2021-06-05', '2021-06-09'),
       (2, 'oppo', '2021-06-11', '2021-06-21'),
       (3, 'vivo', '2021-06-05', '2021-06-15'),
       (4, 'vivo', '2021-06-09', '2021-06-21'),
       (5, 'redmi', '2021-06-05', '2021-06-21'),
       (6, 'redmi', '2021-06-09', '2021-06-15'),
       (7, 'redmi', '2021-06-17', '2021-06-26'),
       (8, 'huawei', '2021-06-05', '2021-06-26'),
       (9, 'huawei', '2021-06-09', '2021-06-15'),
       (10, 'huawei', '2021-06-17', '2021-06-21')
;


select brand, sum(julianday(end_date) - julianday(start_date) + 1) as promotion_day_count
from (select
             brand,
             end_date,
             pre_end_date,
             -- 2. 如果是首次活动或之前最大结束时间小于当前活动开始时间，则取开始时间，否则（最大结束时间大于当前开始时间）取之前最大结束时间+1
--                                 -----=============
--              =========
--                            ==========
--                  ---
             case when pre_end_date is null or pre_end_date < start_date then start_date else date(pre_end_date, '+1 day') end as start_date
      from (select promotion_id
                 , brand
                 , start_date
                 , end_date
                 -- 1. 获取本次活动开始之前上次活动最大结束日期
                 , max(end_date) over (partition by brand order by start_date rows between unbounded preceding and 1 preceding) as pre_end_date
            from promotion_info) t1) t2
where end_date > start_date
group by brand
;


drop table if exists Main;

create table Main(
    id TEXT,
    dt NUMERIC,
    lowcarbon int
);

-- delete from Main where 1 = 1;

insert into Main values
('1001','2021-12-12',	123 ),
('1002','2021-12-12',	45 ),
('1001','2021-12-13',	43 ),
('1001','2021-12-13',	45 ),
('1001','2021-12-13',	23 ),
('1002','2021-12-14',	45 ),
('1001','2021-12-14',	230 ),
('1002','2021-12-15',	45 ),
('1001','2021-12-15',	23 )
;

select * from Main;

-- 计算每个用户当天减少碳排放量
  select id, dt, sum(lowcarbon) as lowcarbon
       --,row_number() over(partition by id,dt order by lowcarbon)
    from Main
group by id, dt
having sum(lowcarbon)  > 100;

-- 等差数列法:两个等差数列如果等差相同,则相同位置的数据相减等到的结果相同
-- 开窗：按用户分组，时间排序，计算rank值
  select id, dt, sum(lowcarbon) as lowcarbon
         ,rank() over(partition by id order by dt) as rank
    from Main
group by id, dt
having sum(lowcarbon) > 100;

-- select dt,date(dt, '+1 day') from Main;

-- 时间减去rank值
select id, date(dt, '-'|| rank ||' day') as dt,/* dt, rank,*/ lowcarbon
from
(
select id, dt, sum(lowcarbon) as lowcarbon
     ,rank() over(partition by id order by dt) as rank
from Main
group by id, dt
having sum(lowcarbon) > 100
) t


-- 根据减完后的时间分组，大于等于3的即为连续3天
select id
from (
    select id, date(dt, '-'|| rank ||' day') as dt, lowcarbon
    from
        (
            select id, dt, sum(lowcarbon) as lowcarbon
                 ,rank() over(partition by id order by dt) as rank
            from Main
            group by id, dt
            having sum(lowcarbon) > 100
        ) t
    ) t2
group by id, dt
having count(1) >= 3;
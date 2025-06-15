--------------------------------------------------- 问题
-- 如下为电商公司用户访问时间数据
-- id	ts(秒)
-- 1001	17523641234
-- 1001	17523641256
-- 1002	17523641278
-- 1001	17523641334
-- 1002	17523641434
-- 1001	17523641534
-- 1001	17523641544
-- 1002	17523641634
-- 1001	17523641638
-- 1001	17523641654
-- 某个用户连续的访问记录如果时间间隔小于 60 秒，则分为同一个组，结果为：
-- id	ts(秒)	group
-- 1001	17523641234	1
-- 1001	17523641256	1
-- 1001	17523641334	2
-- 1001	17523641534	3
-- 1001	17523641544	3
-- 1001	17523641638	4
-- 1001	17523641654	4
-- 1002	17523641278	1
-- 1002	17523641434	2
-- 1002	17523641634	3

drop table if exists Main;
create table Main(
                     id TEXT,
                     ts text
);
-- delete from Main where 1 = 1;
insert into Main values
('1001', '17523641234'),
('1001', '17523641256'),
('1001', '17523641258'),
('1002', '17523641278'),
('1001', '17523641334'),
('1002', '17523641434'),
('1001', '17523641534'),
('1001', '17523641544'),
('1002', '17523641634'),
('1001', '17523641638'),
('1001', '17523641654')
;
select * from Main;

--------------------------------------------------- 解决
-- SELECT SQLITE_VERSION();

/*
-- 有问题
select t.id
       ,t.ts
       ,t.pre_ts
       -- ,t.ts_diff
       ,ifnull(t.ts - pre_ts, 0) as ts_diff
       ,case when ifnull(t.ts - t.pre_ts, 0) <= 60 and t.pre_ts is not null then t.pre_ts else t.ts end as aaa
       ,dense_rank() over (partition by t.id order by case when ifnull(t.ts - t.pre_ts, 0) <= 60 and t.pre_ts is not null then t.pre_ts else t.ts end) as "group"
from (
        select id
               ,ts
               ,lag(ts) over (partition by id order by ts) as pre_ts
               -- ,ifnull(ts - lag(ts) over (partition by id order by ts), 0) as ts_diff
          from Main
      ) t
order by t.id, t.ts
;
*/


/*
要求：
已知前4列，用sql算出res列，即同一个uid下，上一次is_succ=1 时的 id是谁？
 样例数据：
id time     uid   is_suc res
1 2020-01-01 1 1 Null
2 2020-01-02 1 0 1
3 2020-01-03 1 0 1
4 2020-01-04 1 1 1
5 2020-01-05 1 0 4
6 2020-01-06 2 0 Null
7 2020-01-07 2 1 Null
8 2020-01-08 2 0 7
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121346214
  */
drop table if exists getRes;
create table getRes(
    id int ,
    time date ,
    uid int ,
    is_suc int ,
    res int
);

insert into getRes(id, time, uid, is_suc, res) values
(1, '2020-01-01', 1, 1, Null),
(2, '2020-01-02', 1, 0, 1),
(3, '2020-01-03', 1, 0, 1),
(4, '2020-01-04', 1, 1, 1),
(5, '2020-01-05', 1, 0, 4),
(6, '2020-01-06', 2, 0, Null),
(7, '2020-01-07', 2, 1, Null),
(8, '2020-01-08', 2, 0, 7);

select * from getRes;

-- 1. oracle的lag函数+忽略空值 (未测试, 不知道好使不)
/*
select  id
       ,time
       ,uid
       ,is_suc
       ,lag(case when is_suc = 1 then id else null ignore nulls) over(partition by uid order by id) as res
  from getRes
;
*/


-- 2. 选出所有的is_suc=1, 按uid关联, 取id相减最小的
select  T3.id
       ,max(T3.time) as time
       ,T3.uid                         -- max(T3.uid) as uid
       ,max(T3.is_suc) as is_suc
       -- ,group_concat(T3.id2)        -- 类似oracle的 wm_concat函数
       -- 分组取最大的id2, 就是距离最近的id
       ,max(id2) as res                -- over(partition by uid, id) as res (为啥不好使)
  from (
         select  T1.id
                ,T1.time
                ,T1.uid
                ,T1.is_suc
                ,case when T1.id > T2.id then T2.id else null end as id2   -- 去掉错误项
           from getRes T1
      left join (
                    select *
                     from getRes
                    where is_suc = 1
                ) T2
             on T1.uid = T2.uid
--           where T1.id > T2.id
        ) T3
group by  T3.id, T3.uid
;

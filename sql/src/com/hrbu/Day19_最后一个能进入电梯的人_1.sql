/*
难度中等

SQL架构

表: Queue

+-------------+---------+
| Column Name | Type    |
+-------------+---------+
| person_id   | int     |
| person_name | varchar |
| weight      | int     |
| turn        | int     |
+-------------+---------+
person_id 是这个表的主键。
该表展示了所有等待电梯的人的信息。
表中 person_id 和 turn 列将包含从 1 到 n 的所有数字，其中 n 是表中的行数。
电梯最大载重量为 1000。

写一条 SQL 查询语句查找最后一个能进入电梯且不超过重量限制的 person_name 。题目确保队列中第一位的人可以进入电梯 。

查询结果如下所示 :

Queue 表
+-----------+-------------------+--------+------+
| person_id | person_name       | weight | turn |
+-----------+-------------------+--------+------+
| 5         | George Washington | 250    | 1    |
| 3         | John Adams        | 350    | 2    |
| 6         | Thomas Jefferson  | 400    | 3    |
| 2         | Will Johnliams    | 200    | 4    |
| 4         | Thomas Jefferson  | 175    | 5    |
| 1         | James Elephant    | 500    | 6    |
+-----------+-------------------+--------+------+

Result 表
+-------------------+
| person_name       |
+-------------------+
| Thomas Jefferson  |
+-------------------+

为了简化，Queue 表按 turn 列由小到大排序。
上例中 George Washington(id 5), John Adams(id 3) 和 Thomas Jefferson(id 6) 将可以进入电梯,因为他们的体重和为 250 + 350 + 400 = 1000。
Thomas Jefferson(id 6) 是最后一个体重合适并进入电梯的人。
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121614863
*/

create table Queue(
  person_id   int     ,
  person_name varchar(20) ,
  weight      int     ,
  turn        int
);

insert into Queue(person_id, person_name, weight, turn) values
(5, 'George Washington' ,250,  1 ),
(3, 'John Adams'        ,350,  2 ),
(6, 'Thomas Jefferson'  ,400,  3 ),
(2, 'Will Johnliams'    ,200,  4 ),
(4, 'Thomas Jefferson'  ,175,  5 ),
(1, 'James Elephant'    ,500,  6 );

-- 取小于1000的，并倒叙取第一行
select t.person_name
from
(
    -- 截止自己为止的累计重量
   select q.person_id, q.person_name, sum(q2.weight) as sum_weight
     from queue q
left join queue q2
       on q.turn >= q2.turn
 group by q.person_id, q.person_name
) t
where sum_weight <= 1000
order by sum_weight desc
limit 1
;


  select person_name
    from (
          select person_name
                 ,sum(weight) over(order by turn) as tal
            from queue
         ) t
   where tal <= 1000
order by tal desc
   limit 1;

-- SHOW VARIABLES LIKE 'version';
-- SELECT VERSION();
/*

难度中等

SQL架构

Traffic 表：

+---------------+---------+
| Column Name   | Type    |
+---------------+---------+
| user_id       | int     |
| activity      | enum    |
| activity_date | date    |
+---------------+---------+
该表没有主键，它可能有重复的行。
activity 列是 ENUM 类型，可能取 ('login', 'logout', 'jobs', 'groups', 'homepage') 几个值之一。
编写一个 SQL 查询，以查询从今天起最多 90 天内，每个日期该日期首次登录的用户数。假设今天是 2019-06-30.

查询结果格式如下例所示：

Traffic 表：
+---------+----------+---------------+
| user_id | activity | activity_date |
+---------+----------+---------------+
| 1       | login    | 2019-05-01    |
| 1       | homepage | 2019-05-01    |
| 1       | logout   | 2019-05-01    |
| 2       | login    | 2019-06-21    |
| 2       | logout   | 2019-06-21    |
| 3       | login    | 2019-01-01    |
| 3       | jobs     | 2019-01-01    |
| 3       | logout   | 2019-01-01    |
| 4       | login    | 2019-06-21    |
| 4       | groups   | 2019-06-21    |
| 4       | logout   | 2019-06-21    |
| 5       | login    | 2019-03-01    |
| 5       | logout   | 2019-03-01    |
| 5       | login    | 2019-06-21    |
| 5       | logout   | 2019-06-21    |
+---------+----------+---------------+

Result 表：
+------------+-------------+
| login_date | user_count  |
+------------+-------------+
| 2019-05-01 | 1           |
| 2019-06-21 | 2           |
+------------+-------------+
请注意，我们只关心用户数非零的日期.
ID 为 5 的用户第一次登陆于 2019-03-01，因此他不算在 2019-06-21 的的统计内。
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121477185

*/

create table Traffic (
    user_id       int ,
    activity      enum('login', 'logout', 'jobs', 'groups', 'homepage'),
    activity_date date
);

insert into Traffic (user_id , activity, activity_date) values
(1       , 'login',    '2019-05-01'   ),
(1       , 'homepage', '2019-05-01'   ),
(1       , 'logout',   '2019-05-01'   ),
(2       , 'login',    '2019-06-21'   ),
(2       , 'logout',   '2019-06-21'   ),
(3       , 'login',    '2019-01-01'   ),
(3       , 'jobs',     '2019-01-01'   ),
(3       , 'logout',   '2019-01-01'   ),
(4       , 'login',    '2019-06-21'   ),
(4       , 'groups',   '2019-06-21'   ),
(4       , 'logout',   '2019-06-21'   ),
(5       , 'login',    '2019-03-01'   ),
(5       , 'logout',   '2019-03-01'   ),
(5       , 'login',    '2019-06-21'   ),
(5       , 'logout',   '2019-06-21'   );

-- 先找出各个用户首次登录的数据，再限制90天,按日期进行分组取条数
  select tmp.activity_date
         ,count(1) as user_count
  from
    (
       select t.*
              ,row_number() over (partition by user_id order by activity_date) as rn
         from Traffic t
        where activity = 'login'
    ) tmp
   where tmp.rn = 1
     and tmp.activity_date between date_sub('2019-06-30', interval 90 day) and date_format('2019-06-30','%Y-%m-%d')
group by tmp.activity_date;

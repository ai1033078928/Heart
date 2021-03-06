/*

难度中等

SQL架构

动作表： Actions

+---------------+---------+
| Column Name   | Type    |
+---------------+---------+
| user_id       | int     |
| post_id       | int     |
| action_date   | date    |
| action        | enum    |
| extra         | varchar |
+---------------+---------+
这张表没有主键，并有可能存在重复的行。
action 列的类型是 ENUM，可能的值为 ('view', 'like', 'reaction', 'comment', 'report', 'share')。
extra 列拥有一些可选信息，例如：报告理由（a reason for report）或反应类型（a type of reaction）等。
移除表： Removals

+---------------+---------+
| Column Name   | Type    |
+---------------+---------+
| post_id       | int     |
| remove_date   | date    |
+---------------+---------+
这张表的主键是 post_id。
这张表的每一行表示一个被移除的帖子，原因可能是由于被举报或被管理员审查。
编写一段 SQL 来查找：在被报告为垃圾广告的帖子中，被移除的帖子的每日平均占比，四舍五入到小数点后 2 位。

查询结果的格式如下：

Actions table:
+---------+---------+-------------+--------+--------+
| user_id | post_id | action_date | action | extra  |
+---------+---------+-------------+--------+--------+
| 1       | 1       | 2019-07-01  | view   | null   |
| 1       | 1       | 2019-07-01  | like   | null   |
| 1       | 1       | 2019-07-01  | share  | null   |
| 2       | 2       | 2019-07-04  | view   | null   |
| 2       | 2       | 2019-07-04  | report | spam   |
| 3       | 4       | 2019-07-04  | view   | null   |
| 3       | 4       | 2019-07-04  | report | spam   |
| 4       | 3       | 2019-07-02  | view   | null   |
| 4       | 3       | 2019-07-02  | report | spam   |
| 5       | 2       | 2019-07-03  | view   | null   |
| 5       | 2       | 2019-07-03  | report | racism |
| 5       | 5       | 2019-07-03  | view   | null   |
| 5       | 5       | 2019-07-03  | report | racism |
+---------+---------+-------------+--------+--------+

Removals table:
+---------+-------------+
| post_id | remove_date |
+---------+-------------+
| 2       | 2019-07-20  |
| 3       | 2019-07-18  |
+---------+-------------+

Result table:
+-----------------------+
| average_daily_percent |
+-----------------------+
| 75.00                 |
+-----------------------+
2019-07-04 的垃圾广告移除率是 50%，因为有两张帖子被报告为垃圾广告，但只有一个得到移除。
2019-07-02 的垃圾广告移除率是 100%，因为有一张帖子被举报为垃圾广告并得到移除。
其余几天没有收到垃圾广告的举报，因此平均值为：(50 + 100) / 2 = 75%
注意，输出仅需要一个平均值即可，我们并不关注移除操作的日期。
————————————————
版权声明：本文为CSDN博主「weixin_39032019」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121497125
*/

create table Actions(
    user_id     int    ,
    post_id     int    ,
    action_date date   ,
    action      enum('view', 'like', 'reaction', 'comment', 'report', 'share'),
    extra       varchar(20)
);

insert  into Actions(user_id,post_id,action_date,action,extra) values
(1 ,1 ,'2019-07-01','view'  ,null   ),
(1 ,1 ,'2019-07-01','like'  ,null   ),
(1 ,1 ,'2019-07-01','share' ,null   ),
(2 ,2 ,'2019-07-04','view'  ,null   ),
(2 ,2 ,'2019-07-04','report','spam'   ),
(3 ,4 ,'2019-07-04','view'  ,null   ),
(3 ,4 ,'2019-07-04','report','spam'   ),
(4 ,3 ,'2019-07-02','view'  ,null   ),
(4 ,3 ,'2019-07-02','report','spam'   ),
(5 ,2 ,'2019-07-03','view'  ,null   ),
(5 ,2 ,'2019-07-03','report','racism' ),
(5 ,5 ,'2019-07-03','view'  ,null   ),
(5 ,5 ,'2019-07-03','report','racism' );

create table Removals(
     post_id  int,
     remove_date  date
);

insert into Removals(post_id, remove_date  ) values
(2, '2019-07-20'),
(3, '2019-07-18');

select round(sum(t.avg_daily_percent) / count(t.action_date), 2) as average_daily_percent
from (
         select a.action_date
              , sum(case when b.post_id is not null then 1 else 0 end) / count(a.post_id) as avg_daily_percent
         from (select post_id, action_date from actions where extra = 'spam') a
                  left join removals b
                            on a.post_id = b.post_id
         group by a.action_date
     ) t
;
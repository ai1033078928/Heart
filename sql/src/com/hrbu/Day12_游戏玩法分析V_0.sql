/*

SQL架构

Activity 活动记录表

+--------------+---------+
| Column Name  | Type    |
+--------------+---------+
| player_id    | int     |
| device_id    | int     |
| event_date   | date    |
| games_played | int     |
+--------------+---------+
（player_id，event_date）是此表的主键
这张表显示了某些游戏的玩家的活动情况
每一行是一个玩家的记录，他在某一天使用某个设备注销之前登录并玩了很多游戏（可能是 0）
我们将玩家的安装日期定义为该玩家的第一个登录日。

我们还将某个日期 X 的第 1 天留存时间定义为安装日期为 X 的玩家的数量，他们在 X 之后的一天重新登录，除以安装日期为 X 的玩家的数量，四舍五入到小数点后两位。

编写一个 SQL 查询，报告每个安装日期、当天安装游戏的玩家数量和第一天的留存时间。

查询结果格式如下所示：

Activity 表：
+-----------+-----------+------------+--------------+
| player_id | device_id | event_date | games_played |
+-----------+-----------+------------+--------------+
| 1         | 2         | 2016-03-01 | 5            |
| 1         | 2         | 2016-03-02 | 6            |
| 2         | 3         | 2017-06-25 | 1            |
| 3         | 1         | 2016-03-01 | 0            |
| 3         | 4         | 2016-07-03 | 5            |
+-----------+-----------+------------+--------------+

Result 表：
+------------+----------+----------------+
| install_dt | installs | Day1_retention |
+------------+----------+----------------+
| 2016-03-01 | 2        | 0.50           |
| 2017-06-25 | 1        | 0.00           |
+------------+----------+----------------+
玩家 1 和 3 在 2016-03-01 安装了游戏，
但只有玩家 1 在 2016-03-02 重新登录，所以 2016-03-01 的第一天留存时间是 1/2=0.50

玩家 2 在 2017-06-25 安装了游戏，
但在 2017-06-26 没有重新登录，因此 2017-06-25 的第一天留存时间为 0/1=0.00
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121418527

*/


create table Activity  (
    player_id      int,
    device_id        int,
    event_date      date ,
    games_played     int
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into Activity (player_id , device_id , event_date , games_played ) values
(1         , 2         , '2016-03-01' , 5            ),
(1         , 2         , '2016-03-02' , 6            ),
(2         , 3         , '2017-06-25' , 1            ),
(3         , 1         , '2016-03-01' , 0            ),
(3         , 4         , '2016-07-03' , 5            );



/*
    select a.install_dt,
           count(a.player_id) as installs,
           round(count(b.player_id)/ count(a.player_id),2) as Day1_retention
     from
            (
                select player_id,
                       min(event_date) as install_dt
                  from Activity
              group by player_id
            ) as a
left join Activity as b
       on a.player_id=b.player_id
      and b.event_date-a.install_dt=1
 group by a.install_dt

*/
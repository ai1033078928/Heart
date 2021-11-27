/*
难度困难

SQL架构

支出表: Spending

+-------------+---------+
| Column Name | Type    |
+-------------+---------+
| user_id     | int     |
| spend_date  | date    |
| platform    | enum    |
| amount      | int     |
+-------------+---------+
这张表记录了用户在一个在线购物网站的支出历史，该在线购物平台同时拥有桌面端（'desktop'）和手机端（'mobile'）的应用程序。
这张表的主键是 (user_id, spend_date, platform)。
平台列 platform 是一种 ENUM ，类型为（'desktop', 'mobile'）。
写一段 SQL 来查找每天 仅 使用手机端用户、仅 使用桌面端用户和 同时 使用桌面端和手机端的用户人数和总支出金额。

查询结果格式如下例所示：

Spending table:
+---------+------------+----------+--------+
| user_id | spend_date | platform | amount |
+---------+------------+----------+--------+
| 1       | 2019-07-01 | mobile   | 100    |
| 1       | 2019-07-01 | desktop  | 100    |
| 2       | 2019-07-01 | mobile   | 100    |
| 2       | 2019-07-02 | mobile   | 100    |
| 3       | 2019-07-01 | desktop  | 100    |
| 3       | 2019-07-02 | desktop  | 100    |
+---------+------------+----------+--------+

Result table:
+------------+----------+--------------+-------------+
| spend_date | platform | total_amount | total_users |
+------------+----------+--------------+-------------+
| 2019-07-01 | desktop  | 100          | 1           |
| 2019-07-01 | mobile   | 100          | 1           |
| 2019-07-01 | both     | 200          | 1           |
| 2019-07-02 | desktop  | 100          | 1           |
| 2019-07-02 | mobile   | 100          | 1           |
| 2019-07-02 | both     | 0            | 0           |
+------------+----------+--------------+-------------+
在 2019-07-01, 用户1 同时 使用桌面端和手机端购买, 用户2 仅 使用了手机端购买，而用户3 仅 使用了桌面端购买。
在 2019-07-02, 用户2 仅 使用了手机端购买, 用户3 仅 使用了桌面端购买，且没有用户 同时 使用桌面端和手机端购买。
————————————————
版权声明：本文为CSDN博主「weixin_39032019」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121497254
*/

create table Spending (
      user_id    int ,
      spend_date date,
      platform   enum('desktop', 'mobile'),
      amount     int
);


insert into Spending(user_id, spend_date, platform, amount) values
(1 ,'2019-07-01', 'mobile',100),
(1 ,'2019-07-01', 'desktop',100),
(2 ,'2019-07-01', 'mobile',100),
(2 ,'2019-07-02', 'mobile',100),
(3 ,'2019-07-01', 'desktop',100),
(3 ,'2019-07-02', 'desktop',100);

select  t.spend_date
       ,t.platform
       ,sum(t.amount) as total_amount
       ,sum(t.num_suer) as total_users
from (
         -- 根据用户id和时间分组，算出每个用户在每天  使用桌面端还是手机端购买，如果都使用，则置为both
         select
              user_id
              ,spend_date
              ,case when count(1) = 1 then max(platform) else 'both' end as platform
              ,sum(amount) as amount
              ,1 as num_suer                       -- 用于计算用户数
         from Spending
         group by user_id
                ,spend_date
         union all
         -- 上面一段只能显示出存在的情况，为了拼出当天用户数和支出金额都为0的情况
         -- 用户id + 日期与（mobile desktop both三行数据）做笛卡尔积得出所有情况
            select a.user_id, a.spend_date, b.platform, 0 as amount, 0 as num_suer
              from (select user_id,spend_date from Spending group by user_id,spend_date) a
         left join (
                    -- 此段为百度，炸裂一段字符，这里得出mobile desktop both三行数据
                    SELECT substring_index(substring_index(a.chain,'_',b.help_topic_id + 1    ), '_' , -1) AS platform
                      FROM (select 'mobile_desktop_both' as chain) a
                      JOIN mysql.help_topic b
                        ON b.help_topic_id < (length(a.chain) - length( replace(a.chain, '_', '')  ) + 1)
                   ) b
                on 1=1
         ) t
group by  t.spend_date
         ,t.platform
order by t.spend_date;

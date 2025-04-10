/*
难度中等

SQL架构

部门表 Department：

+---------------+---------+
| Column Name   | Type    |
+---------------+---------+
| id            | int     |
| revenue       | int     |
| month         | varchar |
+---------------+---------+
(id, month) 是表的联合主键。
这个表格有关于每个部门每月收入的信息。
月份（month）可以取下列值 ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]。
编写一个 SQL 查询来重新格式化表，使得新的表中有一个部门 id 列和一些对应 每个月 的收入（revenue）列。

查询结果格式如下面的示例所示：

Department 表：
+------+---------+-------+
| id   | revenue | month |
+------+---------+-------+
| 1    | 8000    | Jan   |
| 2    | 9000    | Jan   |
| 3    | 10000   | Feb   |
| 1    | 7000    | Feb   |
| 1    | 6000    | Mar   |
+------+---------+-------+

查询得到的结果表：
+------+-------------+-------------+-------------+-----+-------------+
| id   | Jan_Revenue | Feb_Revenue | Mar_Revenue | ... | Dec_Revenue |
+------+-------------+-------------+-------------+-----+-------------+
| 1    | 8000        | 7000        | 6000        | ... | null        |
| 2    | 9000        | null        | null        | ... | null        |
| 3    | null        | 10000       | null        | ... | null        |
+------+-------------+-------------+-------------+-----+-------------+

注意，结果表有 13 列 (1个部门 id 列 + 12个月份的收入列)。
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121497439
*/


create table Department(
   id       int     ,
   revenue  int     ,
   month    varchar(10)
);

insert into Department(id , revenue ,month) values
(1  , 8000  , 'Jan'  ),
(2  , 9000  , 'Jan'  ),
(3  , 10000 , 'Feb'  ),
(1  , 7000  , 'Feb'  ),
(1  , 6000  , 'Mar'  );


/*1.*/
  select id
         ,sum(case when month = 'Jan' then revenue end) as Jan_Revenue
         ,sum(case when month = 'Feb' then revenue end) as Feb_Revenue
         ,sum(case when month = 'Mar' then revenue end) as Mar_Revenue
         ,sum(case when month = 'Apr' then revenue end) as Apr_Revenue
         ,sum(case when month = 'May' then revenue end) as May_Revenue
         ,sum(case when month = 'Jun' then revenue end) as Jun_Revenue
         ,sum(case when month = 'Jul' then revenue end) as Jul_Revenue
         ,sum(case when month = 'Aug' then revenue end) as Aug_Revenue
         ,sum(case when month = 'Sep' then revenue end) as Sep_Revenue
         ,sum(case when month = 'Oct' then revenue end) as Oct_Revenue
         ,sum(case when month = 'Nov' then revenue end) as Nov_Revenue
         ,sum(case when month = 'Dec' then revenue end) as Dec_Revenue
  from Department
group by id;



/*2 oracle 透视表*/
/*select *
from Department t
    pivot
(
    sum(t.revenue)
    for month in ('Jan' Jan_Revenue,'Feb' Feb_Revenue,'Mar' Mar_Revenue,'Apr' Apr_Revenue,'May' May_Revenue,'Jun' Jun_Revenue,'Jul' Jul_Revenue,'Aug' Aug_Revenue,'Sep' Sep_Revenue,'Oct' Oct_Revenue,'Nov' Nov_Revenue,'Dec' Dec_Revenue)
)
;*/

/*

难度中等

SQL架构

事件表：Events

+---------------+---------+
| Column Name   | Type    |
+---------------+---------+
| business_id   | int     |
| event_type    | varchar |
| occurences    | int     |
+---------------+---------+
此表的主键是 (business_id, event_type)。
表中的每一行记录了某种类型的事件在某些业务中多次发生的信息。
写一段 SQL 来查询所有活跃的业务。

如果一个业务的某个事件类型的发生次数大于此事件类型在所有业务中的平均发生次数，并且该业务至少有两个这样的事件类型，那么该业务就可被看做是活跃业务。

查询结果格式如下所示：

Events table:
+-------------+------------+------------+
| business_id | event_type | occurences |
+-------------+------------+------------+
| 1           | reviews    | 7          |
| 3           | reviews    | 3          |
| 1           | ads        | 11         |
| 2           | ads        | 7          |
| 3           | ads        | 6          |
| 1           | page views | 3          |
| 2           | page views | 12         |
+-------------+------------+------------+

结果表
+-------------+
| business_id |
+-------------+
| 1           |
+-------------+
'reviews'、 'ads' 和 'page views' 的总平均发生次数分别是 (7+3)/2=5, (11+7+6)/3=8, (3+12)/2=7.5。

id 为 1 的业务有 7 个 'reviews' 事件（大于 5）
和 11 个 'ads' 事件（大于 8），所以它是活跃业务。
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121497200

*/

create table Events(
   business_id int    ,
   event_type  varchar(20),
   occurences  int
);

insert into Events(business_id , event_type, occurences) values
(1 ,'reviews'   ,7 ),
(3 ,'reviews'   ,3 ),
(1 ,'ads'       ,11),
(2 ,'ads'       ,7 ),
(3 ,'ads'       ,6 ),
(1 ,'page views',3 ),
(2 ,'page views',12);


select t.business_id
from (
         select business_id
              ,sum( case when a.occurences > b.avg_occurences then 1 else 0 end ) as num
         from Events a
    left join (
             select event_type, avg(occurences) as avg_occurences
             from Events
             group by event_type
         ) b
          on a.event_type = b.event_type
         group by business_id
     ) t
where t.num > 1;

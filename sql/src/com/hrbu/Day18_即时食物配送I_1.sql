/*
难度简单

SQL架构

配送表: Delivery

+-----------------------------+---------+
| Column Name                 | Type    |
+-----------------------------+---------+
| delivery_id                 | int     |
| customer_id                 | int     |
| order_date                  | date    |
| customer_pref_delivery_date | date    |
+-----------------------------+---------+
delivery_id 是表的主键。
该表保存着顾客的食物配送信息，顾客在某个日期下了订单，并指定了一个期望的配送日期（和下单日期相同或者在那之后）。
如果顾客期望的配送日期和下单日期相同，则该订单称为 「即时订单」，否则称为「计划订单」。

写一条 SQL 查询语句获取即时订单所占的百分比， 保留两位小数。

查询结果如下所示：

Delivery 表:
+-------------+-------------+------------+-----------------------------+
| delivery_id | customer_id | order_date | customer_pref_delivery_date |
+-------------+-------------+------------+-----------------------------+
| 1           | 1           | 2019-08-01 | 2019-08-02                  |
| 2           | 5           | 2019-08-02 | 2019-08-02                  |
| 3           | 1           | 2019-08-11 | 2019-08-11                  |
| 4           | 3           | 2019-08-24 | 2019-08-26                  |
| 5           | 4           | 2019-08-21 | 2019-08-22                  |
| 6           | 2           | 2019-08-11 | 2019-08-13                  |
+-------------+-------------+------------+-----------------------------+

Result 表:
+----------------------+
| immediate_percentage |
+----------------------+
| 33.33                |
+----------------------+
2 和 3 号订单为即时订单，其他的为计划订单。
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121614363
*/

create table Delivery(
    delivery_id                 int ,
    customer_id                 int ,
    order_date                  date,
    customer_pref_delivery_date date
);


insert into Delivery(delivery_id, customer_id, order_date, customer_pref_delivery_date) values
(1, 1, '2019-08-01', '2019-08-02'),
(2, 5, '2019-08-02', '2019-08-02'),
(3, 1, '2019-08-11', '2019-08-11'),
(4, 3, '2019-08-24', '2019-08-26'),
(5, 4, '2019-08-21', '2019-08-22'),
(6, 2, '2019-08-11', '2019-08-13');

select round( sum(case when order_date = customer_pref_delivery_date then 1 else 0 end) * 100 / count(1) , 2)  as immediate_percentage
  from Delivery;

-- truncate table Delivery;
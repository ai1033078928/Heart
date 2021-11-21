/*
难度简单

SQL架构

销售表：Sales

+-------------+-------+
| Column Name | Type  |
+-------------+-------+
| sale_id     | int   |
| product_id  | int   |
| year        | int   |
| quantity    | int   |
| price       | int   |
+-------------+-------+
sale_id 是这个表的主键。
product_id 是 Product 表的外键。
请注意价格是每单位的。
产品表：Product

+--------------+---------+
| Column Name  | Type    |
+--------------+---------+
| product_id   | int     |
| product_name | varchar |
+--------------+---------+
product_id 是这个表的主键。
编写一个 SQL 查询，按产品 id product_id 来统计每个产品的销售总量。

查询结果格式如下面例子所示:

Sales 表：
+---------+------------+------+----------+-------+
| sale_id | product_id | year | quantity | price |
+---------+------------+------+----------+-------+
| 1       | 100        | 2008 | 10       | 5000  |
| 2       | 100        | 2009 | 12       | 5000  |
| 7       | 200        | 2011 | 15       | 9000  |
+---------+------------+------+----------+-------+

Product 表：
+------------+--------------+
| product_id | product_name |
+------------+--------------+
| 100        | Nokia        |
| 200        | Apple        |
| 300        | Samsung      |
+------------+--------------+

Result 表：
+--------------+----------------+
| product_id   | total_quantity |
+--------------+----------------+
| 100          | 22             |
| 200          | 15             |
+--------------+----------------+
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121359176
 */

/*CREATE TABLE IF NOT EXISTS `runoob_tbl`(
   `runoob_id` INT UNSIGNED AUTO_INCREMENT,
   `runoob_title` VARCHAR(100) NOT NULL,
   `runoob_author` VARCHAR(40) NOT NULL,
   `submission_date` DATE,
   PRIMARY KEY ( `runoob_id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;*/
create table Sales (
    sale_id     int  PRIMARY KEY,
    product_id  int ,
    year        int ,
    quantity    int ,
    price       int
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into Sales ( sale_id , product_id,  year , quantity , price) values
( 1       , 100     ,    2008 , 10       , 5000 ),
( 2       , 100     ,    2009 , 12       , 5000 ),
( 7       , 200     ,    2011 , 15       , 9000 );

alter table Product rename to Product_1;

create table Product
(
    product_id   int PRIMARY KEY,
    product_name varchar(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into Product (product_id , product_name ) values
(100        , 'Nokia'        ),
(200        , 'Apple'        ),
(300        , 'Samsung'    );

#2.
select product_id, sum(quantity) as total_quantity
from Sales
group by product_id;


#3.
select
         t.product_id
        ,t.first_year
        ,t.quantity
        ,t.price
from (
        select product_id
               ,year as first_year
               ,quantity
               ,price
               ,row_number() over (partition by product_id order by year asc) as rn
        from   Sales ) t
where t.rn = 1

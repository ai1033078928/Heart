/*
难度中等

year month amount
1991 1 1.1
1991 2 1.2
1991 3 1.3
1991 4 1.4
1992 1 2.1
1992 2 2.2
1992 3 2.3
1992 4 2.4


查成这样一个结果？
year m1 m2 m3 m4
1991 1.1 1.2 1.3 1.4
1992 2.1 2.2 2.3 2.4
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121614560
*/

create table row_to_column(
    year varchar(4),
    month int,
    amount decimal(18,4)
);

insert into row_to_column(year, month, amount)  values
('1991', 1, 1.1),
('1991', 2, 1.2),
('1991', 3, 1.3),
('1991', 4, 1.4),
('1992', 1, 2.1),
('1992', 2, 2.2),
('1992', 3, 2.3),
('1992', 4, 2.4);


-- 分组判断case when
select
     year
    ,sum(case when month = 1 then amount else 0 end) as m1
    ,sum(case when month = 2 then amount else 0 end) as m2
    ,sum(case when month = 3 then amount else 0 end) as m3
    ,sum(case when month = 4 then amount else 0 end) as m4
from row_to_column
group by year;



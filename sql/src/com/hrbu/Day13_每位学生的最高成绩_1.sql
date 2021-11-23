/*

SQL架构

表：Enrollments

+---------------+---------+
| Column Name   | Type    |
+---------------+---------+
| student_id    | int     |
| course_id     | int     |
| grade         | int     |
+---------------+---------+
(student_id, course_id) 是该表的主键。
编写一个 SQL 查询，查询每位学生获得的最高成绩和它所对应的科目，若科目成绩并列，取 course_id 最小的一门。查询结果需按 student_id 增序进行排序。

查询结果格式如下所示：

Enrollments 表：
+------------+-------------------+
| student_id | course_id | grade |
+------------+-----------+-------+
| 2          | 2         | 95    |
| 2          | 3         | 95    |
| 1          | 1         | 90    |
| 1          | 2         | 99    |
| 3          | 1         | 80    |
| 3          | 2         | 75    |
| 3          | 3         | 82    |
+------------+-----------+-------+

Result 表：
+------------+-------------------+
| student_id | course_id | grade |
+------------+-----------+-------+
| 1          | 2         | 99    |
| 2          | 2         | 95    |
| 3          | 3         | 82    |
+------------+-----------+-------+
提示：开窗
————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121477225

*/

create table Enrollments
(
    student_id   int ,
    course_id    int ,
    grade        int
) ;

insert into Enrollments (student_id , course_id , grade) values
(2          , 2         , 95   ),
(2          , 3         , 95   ),
(1          , 1         , 90   ),
(1          , 2         , 99   ),
(3          , 1         , 80   ),
(3          , 2         , 75   ),
(3          , 3         , 82   );

select student_id,course_id,grade
from
(
select student_id
       ,course_id
       ,grade
       ,row_number() over(partition by student_id order by grade desc,course_id) as rn
  from Enrollments
) t
where t.rn = 1
order by t.student_id
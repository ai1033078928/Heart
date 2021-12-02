/*
❤ 一、测试数据准备
为方便上机实验，小伙伴们可直接执行一下代码，生成测试数据！

🍅 数据表介绍
--1.成绩表
SC(SId,CId,score)
--SId 学生编号,CId 课程编号,score 分数

--2.学生表
Student(SId,Sname,Sage,Ssex)
--SId 学生编号,Sname 学生姓名,Sage 出生年月,Ssex 学生性别

--3.课程表
Course(CId,Cname,TId)
--CId 课程编号,Cname 课程名称,TId 教师编号

--4.教师表
Teacher(TId,Tname)
--TId 教师编号,Tname 教师姓名

😋 学生表 Student
create table Student(SId varchar(10),Sname varchar(10),Sage datetime,Ssex varchar(10));
insert into Student values('01' , '西红柿' , '1990-01-01' , '男');
insert into Student values('02' , '钱电' , '1990-12-21' , '男');
insert into Student values('03' , '孙风' , '1990-12-20' , '男');
insert into Student values('04' , '李云' , '1990-12-06' , '男');
insert into Student values('05' , '周梅' , '1991-12-01' , '女');
insert into Student values('06' , '吴兰' , '1992-01-01' , '女');
insert into Student values('07' , '郑竹' , '1989-01-01' , '女');
insert into Student values('09' , '张三' , '2017-12-20' , '女');
insert into Student values('10' , '西红柿' , '2017-12-25' , '女');
insert into Student values('11' , '李四' , '2012-06-06' , '女');
insert into Student values('12' , '赵六' , '2013-06-13' , '女');
insert into Student values('13' , '孙七' , '2014-06-01' , '女');
😋 科目表 Course
create table Course(CId varchar(10),Cname nvarchar(10),TId varchar(10));
insert into Course values('01' , '语文' , '02');
insert into Course values('02' , '数学' , '01');
insert into Course values('03' , '英语' , '03');
😋 教师表 Teacher
create table Teacher(TId varchar(10),Tname varchar(10));
insert into Teacher values('01' , '张三');
insert into Teacher values('02' , '李四');
insert into Teacher values('03' , '王五');
insert into Teacher values('04' , '西红柿');
😋 成绩表 SC
create table SC(SId varchar(10),CId varchar(10),score decimal(18,1));
insert into SC values('08' , '03' , 95);
insert into SC values('08' , '03' , 94);
insert into SC values('01' , '01' , 80);
insert into SC values('01' , '02' , 90);
insert into SC values('01' , '03' , 99);
insert into SC values('02' , '01' , 70);
insert into SC values('02' , '02' , 60);
insert into SC values('02' , '03' , 80);
insert into SC values('03' , '01' , 80);
insert into SC values('03' , '02' , 80);
insert into SC values('03' , '03' , 80);
insert into SC values('04' , '01' , 50);
insert into SC values('04' , '02' , 30);
insert into SC values('04' , '03' , 20);
insert into SC values('05' , '01' , 76);
insert into SC values('05' , '02' , 87);
insert into SC values('06' , '01' , 31);
insert into SC values('06' , '03' , 34);
insert into SC values('07' , '02' , 89);
insert into SC values('07' , '03' , 98);

————————————————
版权声明：本文为CSDN博主「不吃西红柿丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_39032019/article/details/121497662
*/


-- ❤ 二、30道题，战个痛快！
-- 1. 查询" 01 "课程比" 02 "课程成绩高的学生的信息及课程分数 (1) 关联join (2)行转列后计算
-- 2. 查询平均成绩大于等于 60 分的同学的学生编号和学生姓名和平均成绩
-- 3. 查询所有同学的学生编号、学生姓名、选课总数、所有课程的总成绩(没成绩的显示为 null )
-- 4. 查询学过「张三」老师授课的同学的信息
-- 5. 查询没有学全所有课程的同学的信息
-- 6. 查询和" 01 "号的同学学习的课程 完全相同的其他同学的信息
-- 7. 查询两门及其以上不及格课程的同学的学号，姓名及其平均成绩
-- 8. 检索" 01 "课程分数小于 60，按分数降序排列的学生信息
-- 9. 按平均成绩从高到低显示所有学生的所有课程的成绩以及平均成绩
-- 10. 查询各科成绩最高分、最低分和平均分：
-- 11. 以如下形式显示：课程 ID，课程 name，最高分，最低分，平均分，及格率，中等率，优良率，优秀率
-- 12. 及格为>=60，中等为：70-80，优良为：80-90，优秀为：>=90
-- 13. 要求输出课程号和选修人数，查询结果按人数降序排列，若人数相同，按课程号升序排列
-- 14. 按各科成绩进行排序，并显示排名， Score 重复时保留名次空缺
-- 15. 统计各科成绩各分数段人数：课程编号，课程名称，[100-85]，[85-70]，[70-60]，[60-0] 及所占百分比
-- 16. 查询各科成绩前三名的记录
-- 17. 查询每门课程被选修的学生数
-- 18. 查询出只选修两门课程的学生学号和姓名
-- 19. 查询同名同性学生名单，并统计同名人数
-- 20. 查询每门课程的平均成绩，结果按平均成绩降序排列，平均成绩相同时，按课程编号升序排列
-- 21. 查询平均成绩大于等于 85 的所有学生的学号、姓名和平均成绩
-- 22. 查询所有学生的课程及分数情况（存在学生没成绩，没选课的情况）
-- 23. 查询任何一门课程成绩在 70 分以上的姓名、课程名称和分数
-- 24. 查询课程编号为 01 且课程成绩在 80 分以上的学生的学号和姓名 求每门课程的学生人数
-- 25. 成绩不重复，查询选修「张三」老师所授课程的学生中，成绩最高的学生信息及其成绩 成绩有重复的情况下，查询选修「张三」老师所授课程的学生中，成绩最高的学生信息及其成绩
-- 26. 查询不同课程成绩相同的学生的学生编号、课程编号、学生成绩
-- 27. 查询每门功成绩最好的前两名 统计每门课程的学生选修人数（超过 5 人的课程才统计）。
-- 28. 检索至少选修两门课程的学生学号
-- 29. 查询选修了全部课程的学生信息
-- 30. 查询各学生的年龄，只按年份来算



-- ❤ 二、30道题，战个痛快！
-- 1. 查询平均成绩大于等于 60 分的同学的学生编号和学生姓名和平均成绩
  select sc.SId, stu.Sname, sc.avg_score
    from (
              select SId, avg(score) as avg_score
                from sc
            group by SId
           having avg(score) >= 60
          ) sc
left join student stu
       on sc.SId = stu.SId;

-- 2. 查询" 01 "课程比" 02 "课程成绩高的学生的信息及课程分数 (1) 关联join (2)行转列后计算
   select stu.*
          ,sc.col01
          ,sc.col02
     from (
            select SId
                   ,sum(case when CId = '01' then score else 0 end) as col01
                   ,sum(case when CId = '02' then score else 0 end) as col02
              from sc
          group by SId
          ) sc
left join student stu
       on sc.SId = stu.SId
    where sc.col01 > sc.col02
;
-- 3. 查询所有同学的学生编号、学生姓名、选课总数、所有课程的总成绩(没成绩的显示为 null )
   select stu.SId
          ,stu.Sname
          ,count(*) as num_course
          ,sum(sc.score) as sum_score
     from student stu
left join sc
       on stu.SId = sc.SId
 group by stu.SId
          ,stu.Sname
;

-- 4. 查询学过「张三」老师授课的同学的信息
   select stu.*
     from student stu
    where stu.SId in (
            select SId
              from sc
         left join course cou
                on sc.CId = cou.CId
         left join teacher tea
                on cou.TId = tea.TId
               and tea.Tname = '张三'
             where tea.TId is not null
        )
;
-- 5. 查询没有学全所有课程的同学的信息
-- 6. 查询和" 01 "号的同学学习的课程 完全相同的其他同学的信息
-- 7. 查询两门及其以上不及格课程的同学的学号，姓名及其平均成绩
-- 8. 检索" 01 "课程分数小于 60，按分数降序排列的学生信息
-- 9. 按平均成绩从高到低显示所有学生的所有课程的成绩以及平均成绩
-- 10. 查询各科成绩最高分、最低分和平均分：
-- 11. 以如下形式显示：课程 ID，课程 name，最高分，最低分，平均分，及格率，中等率，优良率，优秀率
-- 12. 及格为>=60，中等为：70-80，优良为：80-90，优秀为：>=90
-- 13. 要求输出课程号和选修人数，查询结果按人数降序排列，若人数相同，按课程号升序排列
-- 14. 按各科成绩进行排序，并显示排名， Score 重复时保留名次空缺
-- 15. 统计各科成绩各分数段人数：课程编号，课程名称，[100-85]，[85-70]，[70-60]，[60-0] 及所占百分比
-- 16. 查询各科成绩前三名的记录
-- 17. 查询每门课程被选修的学生数
-- 18. 查询出只选修两门课程的学生学号和姓名
-- 19. 查询同名同性学生名单，并统计同名人数
-- 20. 查询每门课程的平均成绩，结果按平均成绩降序排列，平均成绩相同时，按课程编号升序排列
-- 21. 查询平均成绩大于等于 85 的所有学生的学号、姓名和平均成绩
-- 22. 查询所有学生的课程及分数情况（存在学生没成绩，没选课的情况）
-- 23. 查询任何一门课程成绩在 70 分以上的姓名、课程名称和分数
-- 24. 查询课程编号为 01 且课程成绩在 80 分以上的学生的学号和姓名 求每门课程的学生人数
-- 25. 成绩不重复，查询选修「张三」老师所授课程的学生中，成绩最高的学生信息及其成绩 成绩有重复的情况下，查询选修「张三」老师所授课程的学生中，成绩最高的学生信息及其成绩
-- 26. 查询不同课程成绩相同的学生的学生编号、课程编号、学生成绩
-- 27. 查询每门功成绩最好的前两名 统计每门课程的学生选修人数（超过 5 人的课程才统计）。
-- 28. 检索至少选修两门课程的学生学号
-- 29. 查询各学生的年龄，只按年份来算
-- 30. 查询选修了全部课程的学生信息
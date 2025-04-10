-- 1. （简单）输出演员表中所有演员的名字和姓氏。
select first_name, last_name from actor;

-- 2. （简单）输出姓氏包含“GEN”的所有演员。
select * from actor where first_name like '%GEN%';
select * from actor where instr(first_name, 'GEN');

-- 3. （简单）演员 HARPO WILLIAMS 意外地以 GROUCHO WILLIAMS 的身份进入了演员表。编写查询以修复记录。
# select * from actor where first_name = 'HARPO' and last_name = 'WILLIAMS';

-- 4. （简单）创建一个名为“actor_name_view”的视图，其中包含“FirstName”和“LastName”列，并将列分别重命名为“first_name_v”和“last_name_v”。
create view actor_name_view as select first_name as first_name_v, last_name as last_name_v from actor;
# select * from actor_name_view;

-- 5. （中等）列出演员的姓氏和拥有该姓氏的演员人数，并且是至少有两个演员的。
   select t1.first_name, count(1) as num
     from (select distinct first_name from actor) t1
left join actor t2
       on t1.first_name = t2.first_name
 group by t1.first_name
   having count(1) > 1
;
-- 6. （中等）哪个演员出演的电影最多？
select t3.first_name, t3.last_name, t3.num
from
(
       select  t1.actor_id
              , max(t1.first_name) as first_name
              , max(t1.last_name) as last_name
              , count(1) as num
         from actor t1
    left join film_actor t2
           on t1.actor_id = t2.actor_id
     group by t1.actor_id
) t3
order by t3.num desc
limit 1
;

-- 7. （中等）哪类电影的时长偏长？（大于所有电影的平均时长就认为是偏长）
   select special_features
     from film t1
left join (select avg(length) as avg_length from film) t2
       on 1 = 1
    where t1.length > t2.avg_length
 group by special_features
;

select * from film;
select * from film_actor;
select * from film_category;
select * from film_text;

-- 8. （中等）年轻家庭的销售一直滞后，您希望针对所有家庭电影进行促销。识别所有归类为家庭电影的电影。


-- 9. （复杂）列出每部电影以及为该电影列出的演员人数。

-- 10. （复杂）查询电影所属的类别、语言和演员人数。
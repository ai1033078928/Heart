create table user(
                     id long primary key ,
                     name varchar(200),
                     sex varchar(10),
                     age int,
                     email varchar(200)
);

insert into user(id, name, sex, age, email) VALUES
                                                (1,'name-1','男',26,'name-1@gmail.com'),
                                                (2,'name-2','男',26,'name-2@gmail.com')
;


/*insert into user(id, name, sex, age, email) VALUES
                                                (1001,'name-1001','女',27,'name-1001@gmail.com'),
                                                (1002,'name-1002','女',27,'name-1002@gmail.com')
;*/
select * from user;
-- drop table user;
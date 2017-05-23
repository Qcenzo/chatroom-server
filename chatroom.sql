--
drop database if exists chatroom;
create database chatroom; 

--
use chatroom;
drop table if exists play_list;
create table play_list 
(
name varchar(100),
creator varchar(100),
time datetime,
type varchar(10)
)engine=InnoDB default charset=utf8;

--
grant select,insert,delete on chatroom.play_list to chatroom_visitor@localhost identified by '123';
flush privileges;
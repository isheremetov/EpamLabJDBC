drop table if exists bills;
drop table if exists subscribes;
drop table if exists services;
drop table if exists users;

create table users (
	id int primary key auto_increment,
	login varchar(16) not null unique,
	password varchar(16) not null,
	username varchar(16) not null,
	role varchar(5) not null,
	active int not null
);

create table services (
	id int primary key auto_increment,
	name varchar(32) not null unique,
	price int not null
);

create table subscribes (
	id int primary key auto_increment,
	userid int not null references users,
	serviceid int not null references services,
	active int not null default 1
);

create table bills (
	id int primary key auto_increment,
	subscribeid int not null references subscribes,
	payed int not null,
	date datetime
);

insert into users (login, password, username, role, active) 
	values('admin', 'admin', 'admin', 'Admin', 1);
insert into users (login, password, username, role, active) 
	values('user', 'user', 'user', 'User', 1);
insert into services (name, price) values('Городские звонки', 100);
insert into services (name, price) values('Междугородние звонки', 300);
insert into services (name, price) values('Звонки на мобильный', 500);
insert into subscribes (userid, serviceid) values(2, 1) ;
insert into subscribes (userid, serviceid) values(2, 2) ;

delimiter $$

drop procedure if exists `makeBills`$$

create procedure `makeBills`()
begin
	declare sid int;
	declare d DATETIME;
	set d = curdate();
	set sid := (select min(id) from subscribes where active > 0);
	while sid is not null do
		insert into bills (subscribeid, payed, date) values (sid, 0, d);
		set sid := (select min(id) from subscribes where id > sid and active > 0);
	end while;
end$$

delimiter ;
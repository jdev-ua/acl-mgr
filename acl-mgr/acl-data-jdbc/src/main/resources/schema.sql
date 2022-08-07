/* Create table for ACL */
create table if not exists acl (
id identity,
name varchar(32) not null unique,
description varchar(255)
);

/* Create table for ACL's obj.types */
create table if not exists obj_type (
id identity,
acl_id bigint not null,
obj_type varchar(32) not null
);

alter table obj_type add foreign key (acl_id) references acl(id) on delete cascade;

/* Create table for ACL's statuses */
create table if not exists status (
id identity,
acl_id bigint not null,
status varchar(128) not null
);

alter table status add foreign key (acl_id) references acl(id) on delete cascade;

/* Create table for ACL's accessors */
create table if not exists accessor (
id identity,
acl_id bigint not null,
name varchar(32) not null,
permit smallint not null,
alias boolean,
svc boolean
);

alter table accessor add foreign key (acl_id) references acl(id) on delete cascade;

/* Create table for accessor's org.levels */
create table if not exists org_level (
id identity,
accessor_id bigint not null,
org_level varchar(2) not null
);

alter table org_level add foreign key (accessor_id) references accessor(id) on delete cascade;

/* Create table for accessor's xpermits */
create table if not exists xpermit (
id identity,
accessor_id bigint not null,
xpermit varchar(20) not null
);

alter table xpermit add foreign key (accessor_id) references accessor(id) on delete cascade;

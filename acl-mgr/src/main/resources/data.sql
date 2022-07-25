/* Clear ACL table */
delete from acl;
/* Clear ACL's obj.types table */
delete from obj_type;
/* Clear ACL's statuses table */
delete from status;

/* Create test ACL data */
insert into acl (name, description) values ('shire','The Shire');
insert into obj_type (acl_id, obj_type) values ((select id from acl where name='shire'),'rba_aps_limit');
insert into status (acl_id, status) values ((select id from acl where name='shire'),'LIM_S_NEW');
insert into status (acl_id, status) values ((select id from acl where name='shire'),'LIM_S_ACTIVE');
insert into accessor (acl_id, name, permit, alias, svc) values ((select id from acl where name='shire'),'dm_world',3,false,false);

insert into accessor (acl_id, name, permit, alias, svc) values ((select id from acl where name='shire'),'dm_owner',7,false,false);

insert into accessor (acl_id, name, permit, alias, svc) values ((select id from acl where name='shire'),'sauron',1,true,true);
insert into org_level (accessor_id, org_level) values ((select id from accessor where name='sauron'),'co');
insert into org_level (accessor_id, org_level) values ((select id from accessor where name='sauron'),'od');
insert into org_level (accessor_id, org_level) values ((select id from accessor where name='sauron'),'vr');
insert into org_level (accessor_id, org_level) values ((select id from accessor where name='sauron'),'mr');
insert into org_level (accessor_id, org_level) values ((select id from accessor where name='sauron'),'rd');

insert into accessor (acl_id, name, permit, alias, svc) values ((select id from acl where name='shire'),'frodo',6,false,false);
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='frodo'),'CHANGE_LOCATION');

insert into accessor (acl_id, name, permit, alias, svc) values ((select id from acl where name='shire'),'bilbo',6,false,false);
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='bilbo'),'CHANGE_LOCATION');

insert into accessor (acl_id, name, permit, alias, svc) values ((select id from acl where name='shire'),'aragorn',5,true,true);
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='aragorn'),'EXECUTE_PROC');
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='aragorn'),'CHANGE_LOCATION');

insert into accessor (acl_id, name, permit, alias, svc) values ((select id from acl where name='shire'),'gandalf',7,true,false);
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='gandalf'),'EXECUTE_PROC');
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='gandalf'),'CHANGE_LOCATION');
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='gandalf'),'CHANGE_STATE');
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='gandalf'),'CHANGE_PERMIT');
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='gandalf'),'CHANGE_OWNER');
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='gandalf'),'DELETE_OBJECT');
insert into xpermit (accessor_id, xpermit) values ((select id from accessor where name='gandalf'),'CHANGE_FOLDER_LINKS');

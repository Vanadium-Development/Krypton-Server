drop type if exists authmethod;
create type authMethod as enum ('credentials', 'keypair');
alter table "user" rename column password to auth;
alter table "user" add method authMethod;
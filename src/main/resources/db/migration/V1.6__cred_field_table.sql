drop type if exists field_type;
create type field_type as enum ('username', 'password', 'secret', 'otp');

create table cred_field
(
    id      uuid       not null default uuid_generate_v4(),
    type    field_type not null,
    title   text       not null, -- } Encrypted with the public
    value   text       not null, -- } key at all times
    cred_id uuid       not null references credential (id)
);
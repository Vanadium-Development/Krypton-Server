drop type if exists field_type;
create type field_type as enum ('username', 'password', 'secret', 'otp');

create table cred_field
(
    id      uuid       not null default uuid_generate_v4(),
    title   text       not null,
    type    field_type not null,
    value   text       not null, -- Value is encrypted via the public key at all times
    cred_id uuid       not null references credential (id)
);
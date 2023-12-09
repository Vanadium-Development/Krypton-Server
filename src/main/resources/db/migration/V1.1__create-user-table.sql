create table if not exists "user"
(
    id         uuid        not null default uuid_generate_v4() primary key,
    firstname  text        not null,
    lastname   text        not null,
    username   text        not null unique,
    password   text        not null,
    admin      bool        not null default false,
    deleted    bool        not null default false,
    created_at timestamptz not null default now(),
    deleted_at timestamptz          default null
)
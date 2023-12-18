create table if not exists vault
(
    id          uuid not null default uuid_generate_v4() primary key,
    title       text not null,
    description text not null,
    user_id     uuid not null references "user" (id)
);
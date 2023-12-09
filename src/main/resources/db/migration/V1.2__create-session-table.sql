create table if not exists session
(
    token      text        not null primary key,
    created_at timestamptz not null default now(),
    expires_at timestamptz not null,
    invalidate bool,
    user_id    uuid        not null references "user" (id)
);


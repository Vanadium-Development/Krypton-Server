create table if not exists credential
(
    id       uuid not null default uuid_generate_v4() primary key,
    title    text not null,
    vault_id uuid not null references vault (id)
);
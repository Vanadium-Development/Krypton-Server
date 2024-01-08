alter table session drop column expires_at;
alter table session add column accessed_at timestamptz not null default CURRENT_TIMESTAMP;
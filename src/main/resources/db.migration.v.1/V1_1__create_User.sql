CREATE TABLE if NOT EXISTS userTG
(
    chat_id bigserial not null primary key,
    first_name text,
    last_name text,
    user_name text,
    registered_at TIMESTAMPTZ
)
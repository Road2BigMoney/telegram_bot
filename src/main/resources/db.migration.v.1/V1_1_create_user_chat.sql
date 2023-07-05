CREATE TABLE user_chat (
                           user_id BIGINT NOT NULL REFERENCES userTG(id),
                           chat_id BIGINT NOT NULL REFERENCES chatTG(id),
                           PRIMARY KEY (user_id, chat_id)
);
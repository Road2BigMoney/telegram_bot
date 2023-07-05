ALTER TABLE userTG ADD COLUMN id bigserial;
UPDATE userTG SET id = chat_id;
ALTER TABLE usertg ADD CONSTRAINT userTG_chat_id_key UNIQUE (chat_id);
ALTER TABLE usertg DROP CONSTRAINT usertg_pkey;
ALTER TABLE userTG ADD CONSTRAINT pk_userTG_id PRIMARY KEY (id);


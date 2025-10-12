CREATE TABLE conversation_participants
(
    id              UUID NOT NULL,
    conversation_id UUID NOT NULL,
    user_id         UUID NOT NULL,
    CONSTRAINT pk_conversation_participants PRIMARY KEY (id),
        UNIQUE (conversation_id, user_id)
);

CREATE TABLE conversations
(
    id   UUID        NOT NULL,
    type VARCHAR(16) NOT NULL,
    CONSTRAINT pk_conversations PRIMARY KEY (id)
);

ALTER TABLE conversation_participants
    ADD CONSTRAINT FK_CONVERSATION_PARTICIPANTS_ON_CONVERSATION FOREIGN KEY (conversation_id) REFERENCES conversations (id);

ALTER TABLE conversation_participants
    ADD CONSTRAINT FK_CONVERSATION_PARTICIPANTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE messages
    ADD COLUMN conversation_id UUID REFERENCES conversations(id) ON DELETE CASCADE;
CREATE TABLE messages
(
    id        UUID NOT NULL,
    sender_id UUID,
    content   OID,
    CONSTRAINT pk_messages PRIMARY KEY (id)
);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_SENDER FOREIGN KEY (sender_id) REFERENCES users (id);
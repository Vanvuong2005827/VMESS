package com.vuong.vmess.domain.entities;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.service.annotation.GetExchange;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "messages")
public class Message {
    @Id
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "sender_id")
    User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="conversation_id")
    Conversation conversation;

    @Lob
    String content;

    @PrePersist
    void prePersist() {
        if (id == null) id = UuidCreator.getTimeOrderedEpoch();
    }
}

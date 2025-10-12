package com.vuong.vmess.domain.entities;

import com.github.f4b6a3.uuid.UuidCreator;
import com.vuong.vmess.domain.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "conversations")
public class Conversation {
    @Id
    UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    MessageType type; // PRIVATE | GROUP

    @PrePersist
    void prePersist() {
        if (id == null) id = UuidCreator.getTimeOrderedEpoch();
    }
}

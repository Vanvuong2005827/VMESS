package com.vuong.vmess.domain.dto.request.chat;

import com.vuong.vmess.domain.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendMessageRequest {
    String message;
    String conversationId;
    MessageType messageType;
}

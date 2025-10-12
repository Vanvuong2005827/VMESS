package com.vuong.vmess.controller;

import com.vuong.vmess.domain.dto.request.chat.SendMessageRequest;
import com.vuong.vmess.security.CurrentUser;
import com.vuong.vmess.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    //for 1 - 1
    @MessageMapping("/chat.send")
    public void sendMessage(
//            @Parameter(name = "principal", hidden = true) @CurrentUser UserPrincipal principal,
            @Payload SendMessageRequest sendMessageRequest
    ) {
        System.out.println(sendMessageRequest.getMessage());
        messagingTemplate.convertAndSendToUser("999","/topic/chat."+sendMessageRequest.getConversationId(), sendMessageRequest);
    }
}

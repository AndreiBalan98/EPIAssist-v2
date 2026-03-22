package org.epi_assist.EPIAssist_v2.controller;

import org.epi_assist.EPIAssist_v2.dto.ChatRequestDto;
import org.epi_assist.EPIAssist_v2.dto.ChatResponseDto;
import org.epi_assist.EPIAssist_v2.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponseDto chat(
            @RequestBody ChatRequestDto chatRequestDto
    ) {
        return chatService.chat(chatRequestDto);
    }
}

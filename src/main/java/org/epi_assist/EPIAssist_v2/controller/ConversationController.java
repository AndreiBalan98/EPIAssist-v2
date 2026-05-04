package org.epi_assist.EPIAssist_v2.controller;

import org.epi_assist.EPIAssist_v2.dto.ConversationDto;
import org.epi_assist.EPIAssist_v2.service.ConversationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public List<ConversationDto> getConversations() {
        return conversationService.getConversations();
    }
}

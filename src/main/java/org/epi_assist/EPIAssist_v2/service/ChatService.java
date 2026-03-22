package org.epi_assist.EPIAssist_v2.service;

import org.epi_assist.EPIAssist_v2.dto.ChatRequestDto;
import org.epi_assist.EPIAssist_v2.dto.ChatResponseDto;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    public ChatResponseDto chat(ChatRequestDto request) {
        return new ChatResponseDto(request.message());
    }
}

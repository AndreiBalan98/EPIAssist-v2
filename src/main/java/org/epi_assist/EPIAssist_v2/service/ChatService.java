package org.epi_assist.EPIAssist_v2.service;

import org.epi_assist.EPIAssist_v2.dto.ChatRequestDto;
import org.epi_assist.EPIAssist_v2.dto.ChatResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChatService {

    private final RestClient restClient;

    public ChatService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ChatResponseDto chat(ChatRequestDto chatRequestDto) {

        return restClient.post()
                .uri("/ai")
                .contentType(MediaType.APPLICATION_JSON)
                .body(chatRequestDto)
                .retrieve()
                .body(ChatResponseDto.class);
    }
}

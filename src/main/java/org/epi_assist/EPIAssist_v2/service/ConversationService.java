package org.epi_assist.EPIAssist_v2.service;

import org.epi_assist.EPIAssist_v2.dto.ConversationDto;
import org.epi_assist.EPIAssist_v2.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public List<ConversationDto> getConversations() {
        return conversationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(c -> new ConversationDto(
                        c.getId(),
                        c.getUserPrompt(),
                        c.getEnhancedPrompt(),
                        c.getCandidateChunks(),
                        c.getContextChunks(),
                        c.getFinalAnswer(),
                        c.getCreatedAt()
                ))
                .toList();
    }
}

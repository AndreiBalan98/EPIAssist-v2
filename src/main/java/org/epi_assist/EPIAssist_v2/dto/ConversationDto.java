package org.epi_assist.EPIAssist_v2.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ConversationDto(
        Long id,
        String userPrompt,
        String enhancedPrompt,
        List<Map<String, Object>> candidateChunks,
        List<Map<String, Object>> contextChunks,
        String finalAnswer,
        Instant createdAt
) {
}

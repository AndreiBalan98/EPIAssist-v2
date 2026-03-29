package org.epi_assist.EPIAssist_v2.service;

import org.epi_assist.EPIAssist_v2.dto.ChunkDto;
import org.epi_assist.EPIAssist_v2.repository.ChunkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChunkService {

    private final ChunkRepository chunkRepository;

    public ChunkService(ChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }

    public List<ChunkDto> getChunksByDocumentName(String documentName) {
        return chunkRepository.findByUrlStartingWith(documentName + "/")
                .stream()
                .map(chunk -> new ChunkDto(chunk.getId(), chunk.getUrl(), chunk.getContent(), chunk.getEmbedding()))
                .toList();
    }
}

package org.epi_assist.EPIAssist_v2.controller;

import org.epi_assist.EPIAssist_v2.dto.ChunkDto;
import org.epi_assist.EPIAssist_v2.service.ChunkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chunks")
public class ChunkController {

    private final ChunkService chunkService;

    public ChunkController(ChunkService chunkService) {
        this.chunkService = chunkService;
    }

    @GetMapping("/{name}")
    public List<ChunkDto> getChunksByDocument(
            @PathVariable("name") String documentName
    ) {
        return chunkService.getChunksByDocumentName(documentName);
    }
}

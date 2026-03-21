package org.epi_assist.EPIAssist_v2.controller;

import org.epi_assist.EPIAssist_v2.dto.DocumentContentDto;
import org.epi_assist.EPIAssist_v2.dto.DocumentNameDto;
import org.epi_assist.EPIAssist_v2.service.DocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public List<DocumentNameDto> getDocumentsNames() {
        return documentService.getDocumentsNames();
    }

    @GetMapping("/{id}")
    public DocumentContentDto getDocumentContent(
            @PathVariable Long id
    ) {
        return documentService.getDocumentById(id);
    }
}

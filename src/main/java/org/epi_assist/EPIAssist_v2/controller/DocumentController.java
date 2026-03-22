package org.epi_assist.EPIAssist_v2.controller;

import org.epi_assist.EPIAssist_v2.dto.DocumentContentDto;
import org.epi_assist.EPIAssist_v2.dto.DocumentDto;
import org.epi_assist.EPIAssist_v2.dto.DocumentNameDto;
import org.epi_assist.EPIAssist_v2.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{name}")
    public DocumentContentDto getDocumentContent(
            @PathVariable String name
    ) {
        return documentService.getDocumentByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) //201
    public DocumentNameDto postDocument(
            @RequestBody DocumentDto documentDto
    ) {
        return documentService.postDocument(documentDto);
    }
}

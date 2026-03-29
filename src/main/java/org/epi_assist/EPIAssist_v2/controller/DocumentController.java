package org.epi_assist.EPIAssist_v2.controller;

import org.epi_assist.EPIAssist_v2.dto.DocumentContentDto;
import org.epi_assist.EPIAssist_v2.dto.DocumentNameDto;
import org.epi_assist.EPIAssist_v2.dto.TocSectionDto;
import org.epi_assist.EPIAssist_v2.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping("/toc/{name}")
    public List<TocSectionDto> getDocumentToc(
            @PathVariable String name
    ) {
        return documentService.getDocumentToc(name);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable String name) {
        documentService.deleteDocument(name);
    }

    @PostMapping(consumes = "multipart/form-data") //just for strict validation
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentNameDto postDocument(
            @RequestParam String name,
            @RequestParam MultipartFile file
    ) throws IOException {
        return documentService.postDocument(name, file);
    }
}

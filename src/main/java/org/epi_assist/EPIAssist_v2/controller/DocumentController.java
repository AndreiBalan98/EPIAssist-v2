package org.epi_assist.EPIAssist_v2.controller;

import org.epi_assist.EPIAssist_v2.dto.DocumentDto;
import org.epi_assist.EPIAssist_v2.service.DocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentDto> getDocuments() {
        return service.getDocuments();
    }
}

package org.epi_assist.EPIAssist_v2.service;

import org.epi_assist.EPIAssist_v2.dto.DocumentDto;
import org.epi_assist.EPIAssist_v2.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public List<DocumentDto> getDocuments() {
        return repository.findAll()
                .stream()
                .map(document -> new DocumentDto(document.getName()))
                .toList();
    }
}

package org.epi_assist.EPIAssist_v2.service;

import org.epi_assist.EPIAssist_v2.dto.DocumentContentDto;
import org.epi_assist.EPIAssist_v2.dto.DocumentDto;
import org.epi_assist.EPIAssist_v2.dto.DocumentNameDto;
import org.epi_assist.EPIAssist_v2.entity.Document;
import org.epi_assist.EPIAssist_v2.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<DocumentNameDto> getDocumentsNames() {
        return documentRepository.findAll()
                .stream()
                .map(document -> new DocumentNameDto(document.getName()))
                .toList();
    }

    public DocumentContentDto getDocumentByName(String name) {
        return new DocumentContentDto(documentRepository.findByName(name).getMarkdownContent());
    }

    public DocumentNameDto postDocument(DocumentDto documentDto) {
        Document document = new Document();
        document.setName(documentDto.nameDto().name());
        document.setMarkdownContent(documentDto.contentDto().content());
        documentRepository.save(document);
        return new DocumentNameDto(document.getName());
    }
}

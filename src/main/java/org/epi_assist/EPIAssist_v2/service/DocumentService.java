package org.epi_assist.EPIAssist_v2.service;

import org.epi_assist.EPIAssist_v2.dto.DocumentContentDto;
import org.epi_assist.EPIAssist_v2.dto.DocumentNameDto;
import org.epi_assist.EPIAssist_v2.dto.TocSectionDto;
import org.epi_assist.EPIAssist_v2.entity.Document;
import org.epi_assist.EPIAssist_v2.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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

    public List<TocSectionDto> getDocumentToc(String name) {
        return documentRepository.findByName(name).getToc();
    }

    public DocumentNameDto postDocument(String name, MultipartFile file) throws IOException {
        String content = new String(file.getBytes());
        Document document = new Document();
        document.setName(name);
        document.setMarkdownContent(content);
        document.setToc(extractToc(content));
        documentRepository.save(document);
        return new DocumentNameDto(document.getName());
    }

    private List<TocSectionDto> extractToc(String content) {
        List<TocSectionDto> toc = new ArrayList<>();
        int position = 1;
        int i = 0;
        int length = content.length();

        while (i < length) {
            boolean atLineStart = (i == 0) || (content.charAt(i - 1) == '\n');

            if (atLineStart && content.charAt(i) == '#') {
                int level = 0;
                while (i < length && content.charAt(i) == '#') {
                    level++;
                    i++;
                }
                while (i < length && content.charAt(i) == ' ') {
                    i++;
                }
                StringBuilder title = new StringBuilder();
                while (i < length && content.charAt(i) != '\r' && content.charAt(i) != '\n') {
                    title.append(content.charAt(i));
                    i++;
                }
                toc.add(new TocSectionDto(position++, title.toString(), level));
            } else {
                i++;
            }
        }
        return toc;
    }
}

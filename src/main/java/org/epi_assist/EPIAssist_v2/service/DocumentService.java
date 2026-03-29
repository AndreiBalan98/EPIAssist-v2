package org.epi_assist.EPIAssist_v2.service;

import org.epi_assist.EPIAssist_v2.dto.DocumentContentDto;
import org.epi_assist.EPIAssist_v2.dto.DocumentNameDto;
import org.epi_assist.EPIAssist_v2.dto.EmbeddingRequestDto;
import org.epi_assist.EPIAssist_v2.dto.EmbeddingResponseDto;
import org.epi_assist.EPIAssist_v2.dto.TocSectionDto;
import org.epi_assist.EPIAssist_v2.entity.Chunk;
import org.epi_assist.EPIAssist_v2.entity.Document;
import org.epi_assist.EPIAssist_v2.repository.ChunkRepository;
import org.epi_assist.EPIAssist_v2.repository.DocumentRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final RestClient restClient;

    public DocumentService(DocumentRepository documentRepository, ChunkRepository chunkRepository, RestClient restClient) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.restClient = restClient;
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

    @Transactional
    public void deleteDocument(String name) {
        chunkRepository.deleteByUrlStartingWith(name + "/");
        documentRepository.delete(documentRepository.findByName(name));
    }

    public DocumentNameDto postDocument(String name, MultipartFile file) throws IOException {
        String content = new String(file.getBytes());
        Document document = new Document();
        document.setName(name);
        document.setMarkdownContent(content);
        document.setToc(extractToc(content));
        documentRepository.save(document);

        List<Chunk> chunks = extractChunks(name, content);
        for (Chunk chunk : chunks) {
            EmbeddingResponseDto response = restClient.post()
                    .uri("/embedding")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new EmbeddingRequestDto(chunk.getContent()))
                    .retrieve()
                    .body(EmbeddingResponseDto.class);
            if (response != null) {
                chunk.setEmbedding(response.embedding());
            }
            chunkRepository.save(chunk);
        }

        return new DocumentNameDto(document.getName());
    }

    private List<Chunk> extractChunks(String documentName, String content) {
        List<Chunk> chunks = new ArrayList<>();
        String[] lines = content.split("\r\n", -1);
        String[] headingHierarchy = new String[10];
        StringBuilder currentContent = new StringBuilder();
        String currentUrl = null;

        for (String line : lines) {
            if (line.startsWith("#")) {
                if (currentUrl != null) {
                    Chunk chunk = new Chunk();
                    chunk.setUrl(currentUrl);
                    chunk.setContent(currentContent.toString());
                    chunks.add(chunk);
                }

                int level = 0;
                while (level < line.length() && line.charAt(level) == '#') level++;

                int i = level;
                while (i < line.length() && line.charAt(i) == ' ') i++;
                String title = line.substring(i);

                headingHierarchy[level - 1] = title;
                for (int j = level; j < headingHierarchy.length; j++) {
                    headingHierarchy[j] = null;
                }

                StringBuilder url = new StringBuilder(documentName);
                for (int j = 0; j < level; j++) {
                    url.append("/").append(headingHierarchy[j]);
                }
                currentUrl = url.toString();
                currentContent = new StringBuilder();
            } else {
                if (currentUrl != null) {
                    currentContent.append(line).append("\r\n");
                }
            }
        }

        if (currentUrl != null) {
            Chunk chunk = new Chunk();
            chunk.setUrl(currentUrl);
            chunk.setContent(currentContent.toString());
            chunks.add(chunk);
        }

        return chunks;
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

package org.epi_assist.EPIAssist_v2.repository;

import org.epi_assist.EPIAssist_v2.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findByName(String name);
}

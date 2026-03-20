package org.epi_assist.EPIAssist_v2.repository;

import org.epi_assist.EPIAssist_v2.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}

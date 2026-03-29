package org.epi_assist.EPIAssist_v2.repository;

import org.epi_assist.EPIAssist_v2.entity.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChunkRepository extends JpaRepository<Chunk, Long> {
    List<Chunk> findByUrlStartingWith(String prefix);
    void deleteByUrlStartingWith(String prefix);
}

package org.epi_assist.EPIAssist_v2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_prompt", nullable = false, columnDefinition = "TEXT")
    private String userPrompt;

    @Column(name = "enhanced_prompt", columnDefinition = "TEXT")
    private String enhancedPrompt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "candidate_chunks", columnDefinition = "jsonb")
    private List<Map<String, Object>> candidateChunks;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context_chunks", columnDefinition = "jsonb")
    private List<Map<String, Object>> contextChunks;

    @Column(name = "final_answer", columnDefinition = "TEXT")
    private String finalAnswer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

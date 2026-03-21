package org.epi_assist.EPIAssist_v2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Setter
@Getter
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "markdown_content", nullable = false, columnDefinition = "TEXT")
    private String markdownContent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json_content")
    private String jsonContent;
}

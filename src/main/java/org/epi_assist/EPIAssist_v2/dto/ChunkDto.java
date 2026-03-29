package org.epi_assist.EPIAssist_v2.dto;

import java.util.List;

public record ChunkDto(Long id, String url, String content, List<Float> embedding) {
}

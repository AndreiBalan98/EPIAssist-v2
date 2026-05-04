package org.epi_assist.EPIAssist_v2.repository;

import org.epi_assist.EPIAssist_v2.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}

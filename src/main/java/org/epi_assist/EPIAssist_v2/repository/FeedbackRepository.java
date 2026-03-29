package org.epi_assist.EPIAssist_v2.repository;

import org.epi_assist.EPIAssist_v2.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}

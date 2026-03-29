package org.epi_assist.EPIAssist_v2.service;

import org.epi_assist.EPIAssist_v2.dto.FeedbackRequestDto;
import org.epi_assist.EPIAssist_v2.entity.Feedback;
import org.epi_assist.EPIAssist_v2.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public void postFeedback(FeedbackRequestDto feedbackRequestDto) {
        Feedback feedback = new Feedback();
        feedback.setConvo(feedbackRequestDto.convo());
        feedback.setStars(feedbackRequestDto.stars());
        feedback.setMessage(feedbackRequestDto.message());
        feedbackRepository.save(feedback);
    }
}

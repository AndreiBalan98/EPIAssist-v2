package org.epi_assist.EPIAssist_v2.controller;

import org.epi_assist.EPIAssist_v2.dto.FeedbackRequestDto;
import org.epi_assist.EPIAssist_v2.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postFeedback(@RequestBody FeedbackRequestDto feedbackRequestDto) {
        feedbackService.postFeedback(feedbackRequestDto);
    }
}

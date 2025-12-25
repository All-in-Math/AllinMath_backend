package com.allinmath.backend.model.lesson;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LessonStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("declined")
    DECLINED,
    @JsonProperty("scheduled")
    SCHEDULED,
    @JsonProperty("in progress")
    IN_PROGRESS,
    @JsonProperty("completed")
    COMPLETED,
    @JsonProperty("cancelled")
    CANCELLED
}

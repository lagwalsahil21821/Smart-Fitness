package com.fitness.activityservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

import com.fitness.activityservice.model.ActivityType;

@Data
public class ActivityDto {
    private String activityId;
    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
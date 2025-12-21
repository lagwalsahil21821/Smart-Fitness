package com.fitness.activityservice.mapper;

import com.fitness.activityservice.dto.ActivityDto;
import com.fitness.activityservice.dto.ActivityRequestDto;
import com.fitness.activityservice.model.Activity;

public class ActivityMapper {
    public static Activity mapToActivity(ActivityRequestDto activityRequestDto) {
        Activity activity = new Activity();
        activity.setUserId(activityRequestDto.getUserId());
        activity.setType(activityRequestDto.getType());
        activity.setDuration(activityRequestDto.getDuration());
        activity.setStartTime(activityRequestDto.getStartTime());
        activity.setCaloriesBurned(activityRequestDto.getCaloriesBurned());
        activity.setAdditionalMetrics(activityRequestDto.getAdditionalMetrics());

        return activity;
    }

    public static ActivityDto mapToActivityDto(Activity activity) {
        ActivityDto activityDto = new ActivityDto();
        activityDto.setActivityId(activity.getActivityId());
        activityDto.setUserId(activity.getUserId());
        activityDto.setType(activity.getType());
        activityDto.setDuration(activity.getDuration());
        activityDto.setCaloriesBurned(activity.getCaloriesBurned());
        activityDto.setStartTime(activity.getStartTime());
        activityDto.setAdditionalMetrics(activity.getAdditionalMetrics());
        activityDto.setCreatedAt(activity.getCreatedAt());
        activityDto.setUpdatedAt(activity.getUpdatedAt());

        return activityDto;
    }
}

package com.fitness.activityservice.service;

import lombok.AllArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.fitness.activityservice.dto.ActivityDto;
import com.fitness.activityservice.dto.ActivityRequestDto;
import com.fitness.activityservice.mapper.ActivityMapper;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;

@Service
@AllArgsConstructor
public class ActivityService {
    private final ActivityRepository repository;

    public ActivityDto trackActivity(ActivityRequestDto request) {
        Activity activity = ActivityMapper.mapToActivity(request);
        Activity savedActivity = repository.save(activity);
        return ActivityMapper.mapToActivityDto(savedActivity);
    }

    public ActivityDto findByActivityId(String activityId) {
        Activity activity = repository.findById(activityId)
                                .orElseThrow(() -> new RuntimeException("Activity Id doesn't exists"));
        return ActivityMapper.mapToActivityDto(activity);
    }

    public List<ActivityDto> getUserActivities(String userId) {
        List<Activity> activities = repository.findByUserId(userId);
        return activities.stream()
                .map(activity -> ActivityMapper.mapToActivityDto(activity))
                .collect(Collectors.toList());
    }
}
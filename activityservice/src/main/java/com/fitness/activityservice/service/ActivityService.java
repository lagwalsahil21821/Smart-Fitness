package com.fitness.activityservice.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitness.activityservice.dto.ActivityDto;
import com.fitness.activityservice.dto.ActivityRequestDto;
import com.fitness.activityservice.mapper.ActivityMapper;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final ActivityRepository repository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;


    public ActivityDto trackActivity(ActivityRequestDto request) {
        boolean isValidUser = userValidationService.validateUser(request.getUserId());

        if(!isValidUser) {
            throw new RuntimeException("Invalid User ID: " + request.getUserId());
        }

        Activity activity = ActivityMapper.mapToActivity(request);
        Activity savedActivity = repository.save(activity);

        // Publish the activity to RabbitMQ for AI processing
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        } catch (Exception e) {
            log.error("Failed to publish activity to RabbitMQ with error: ", e);
        }

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
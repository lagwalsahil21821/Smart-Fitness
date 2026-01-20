package com.fitness.activityservice.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.activityservice.dto.ActivityDto;
import com.fitness.activityservice.dto.ActivityRequestDto;
import com.fitness.activityservice.service.ActivityService;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<List<ActivityDto>> getUserActivities(@RequestHeader("X-USER-ID") String userId) {
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityDto> findActivityById(@PathVariable String activityId) {
        return ResponseEntity.ok(activityService.findByActivityId(activityId));
    }
    
    @PostMapping
    public ResponseEntity<ActivityDto> trackActivity(@RequestBody ActivityRequestDto request) {
        return ResponseEntity.ok(activityService.trackActivity(request));
    }
}
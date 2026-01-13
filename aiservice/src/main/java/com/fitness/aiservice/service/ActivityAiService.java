package com.fitness.aiservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
  private final GeminiService geminiService;

  public Recommendation genearteRecommendation(Activity activity) {
      String prompt = createPrompt(activity);
      String aiResponse = geminiService.getAnswer(prompt);
      return processAiResponse(activity, aiResponse);
  }

  private String createPrompt(Activity activity) {
      return String.format("""
      Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
      {
        "analysis": {
          "overall": "Overall analysis here",
          "pace": "Pace analysis here",
          "heartRate": "Heart rate analysis here",
          "caloriesBurned": "Calories analysis here"
        },
        "improvements": [
          {
            "area": "Area name",
            "recommendation": "Detailed recommendation"
          }
        ],
        "suggestions": [
          {
            "workout": "Workout name",
            "description": "Detailed workout description"
          }
        ],
        "safety": [
          "Safety point 1",
          "Safety point 2"
        ]
      }

      Analyze this activity:
      Activity Type: %s
      Duration: %d minutes
      Calories Burned: %d
      Additional Metrics: %s
      
      Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
      Ensure the response follows the EXACT JSON format shown above.
      """,
          activity.getType(),
          activity.getDuration(),
          activity.getCaloriesBurned(),
          activity.getAdditionalMetrics()
      );
  }

  private Recommendation processAiResponse(Activity activity, String aiResponse) {
      try {
        ObjectMapper mapper = new ObjectMapper();
        tools.jackson.databind.JsonNode rootNode = mapper.readTree(aiResponse);
        tools.jackson.databind.JsonNode textNode = rootNode.path("candidates")
                                      .get(0)
                                      .path("content")
                                      .path("parts")
                                      .get(0)
                                      .path("text");

        String jsonContent = textNode.asString()
                .replaceAll("```json\\n", "")
                .replaceAll("\\n```", "")
                .trim();

        // log.info("Parsed Response from AI: {}", jsonContent);
        JsonNode analysisJson = mapper.readTree(jsonContent);
        JsonNode analysisNode = analysisJson.path("analysis");

        StringBuilder fullAnalysis = new StringBuilder();
        addAnalysisSection(analysisNode, fullAnalysis, "overall", "Overall:");
        addAnalysisSection(analysisNode, fullAnalysis, "pace", "Pace:");
        addAnalysisSection(analysisNode, fullAnalysis, "heartRate", "Heart Rate:");
        addAnalysisSection(analysisNode, fullAnalysis, "caloriesBurned", "Calories Burned:");

        List<String> improvements = extractImprovements(analysisJson.path("improvements"));
        List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
        List<String> safetyGuidelines = extractSafetyGuidelines(analysisJson.path("safety"));

        return Recommendation.builder()
                .activityId(activity.getActivityId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation(fullAnalysis.toString().trim())
                .improvements(improvements)
                .suggestions(suggestions)
                .safety(safetyGuidelines)
                .createdAt(LocalDateTime.now())
                .build();
      } catch (Exception e) {
        e.printStackTrace();
        return createDefaultRecommendation(activity);
      }
  }

  private void addAnalysisSection(JsonNode analysisNode, StringBuilder fullAnalysis, String key, String prefix) {
    if(!analysisNode.isMissingNode()) {
      fullAnalysis.append(prefix)
      .append(analysisNode.path(key).asString())
      .append("\n\n");
    }
  }

  private List<String> extractImprovements(JsonNode improvementsNode) {
    List<String> improvements = new ArrayList<>();
    if(improvementsNode.isArray()) {
      improvementsNode.forEach(improvement -> {
        String area = improvement.path("area").asString();
        String detail = improvement.path("recommendation").asString();
        improvements.add(String.format("%s: %s", area, detail));
      });
    }

    return improvements.isEmpty() ? Collections.singletonList("No specific improvement provided") : improvements;
  }

  private List<String> extractSuggestions(JsonNode suggestionNode) {
    List<String> suggestions = new ArrayList<>();
    if(suggestionNode.isArray()) {
      suggestionNode.forEach(suggestion -> {
        String workout = suggestion.path("workout").asString();
        String description = suggestion.path("description").asString();
        suggestions.add(String.format("%s: %s", workout, description));
      });
    }

    return suggestions.isEmpty() ? Collections.singletonList("No specific suggestions provided") : suggestions;
  }

  private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
    List<String> safety = new ArrayList<>();
    if(safetyNode.isArray()) {
      safetyNode.forEach(item -> {
        safety.add(item.asString());
      });
    }

    return safety.isEmpty() ? Collections.singletonList("Follow general safety guidelines") : safety;
  }

  private Recommendation createDefaultRecommendation(Activity activity) {
    Recommendation defaultRecommendation = Recommendation.builder()
                .activityId(activity.getActivityId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    return defaultRecommendation;
  }
}

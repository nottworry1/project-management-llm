package org.kupchenko.projectmanagementllm.service.impl;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Comment;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.service.LlmService;
import org.kupchenko.projectmanagementllm.service.SprintAnalysisService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SprintAnalysisServiceAIImpl implements SprintAnalysisService {
    private final LlmService llmService;

    @Override
    public String analyzeSprint(Sprint sprint) {
        try {
            String template = loadTemplate("prompts/sprint-analysis.txt");
            Map<String, String> stats = collectStats(sprint);
            String prompt = populateTemplate(template, stats);
            System.out.println("Prompt: " + prompt);
            String result = llmService.getResponse(prompt);
            System.out.println("Result: " + result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to analyze sprint: cannot load template", e);
        }
    }

    private String loadTemplate(String resourcePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Map<String, String> collectStats(Sprint sprint) {
        List<Task> tasks = sprint.getTasks();
        int plannedCount = tasks.size();
        int completedCount = (int) tasks.stream()
                .filter(t -> "DONE".equalsIgnoreCase(t.getTaskStatus().getName()))
                .count();
        int totalPoints = tasks.stream()
                .mapToInt(t -> Optional.ofNullable(t.getStoryPoints()).orElse(0))
                .sum();
        int donePoints = tasks.stream()
                .filter(t -> "DONE".equalsIgnoreCase(t.getTaskStatus().getName()))
                .mapToInt(t -> Optional.ofNullable(t.getStoryPoints()).orElse(0))
                .sum();
        double avgCycle = tasks.stream()
                .filter(t -> "DONE".equalsIgnoreCase(t.getTaskStatus().getName()))
                .filter(t -> t.getCreatedAt() != null && t.getStatusChangedAt() != null)
                .mapToLong(t -> ChronoUnit.DAYS.between(t.getCreatedAt().toLocalDateTime(), t.getStatusChangedAt()))
                .average()
                .orElse(0.0);
        String blockers = tasks.stream()
                .flatMap(t -> t.getComments().stream())
                .map(Comment::getText)
                .filter(txt -> txt.toLowerCase().contains("block"))
                .limit(5)
                .collect(Collectors.joining("; "));
        List<String> taskDetails = tasks.stream()
                .map(Task::getTaskDetails)
                .toList();
        Map<String, String> vars = new HashMap<>();
        vars.put("name", sprint.getName());
        vars.put("start", sprint.getStartDate().toLocalDate().toString());
        vars.put("end", sprint.getEndDate().toLocalDate().toString());
        vars.put("plannedCount", String.valueOf(plannedCount));
        vars.put("completedCount", String.valueOf(completedCount));
        vars.put("totalPoints", String.valueOf(totalPoints));
        vars.put("donePoints", String.valueOf(donePoints));
        vars.put("avgCycle", String.format("%.1f", avgCycle));
        vars.put("blockerSnippets", blockers);
        vars.put("taskDetails", String.join("\n", taskDetails));
        return vars;
    }

    private String populateTemplate(String template, Map<String, String> vars) {
        String result = template;
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
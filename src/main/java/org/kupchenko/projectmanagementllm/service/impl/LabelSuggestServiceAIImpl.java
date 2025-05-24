package org.kupchenko.projectmanagementllm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.dto.TaskSuggestLabelDto;
import org.kupchenko.projectmanagementllm.model.Comment;
import org.kupchenko.projectmanagementllm.model.Label;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.service.LabelService;
import org.kupchenko.projectmanagementllm.service.LabelSuggestService;
import org.kupchenko.projectmanagementllm.service.LlmService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelSuggestServiceAIImpl implements LabelSuggestService {
    private final LabelService labelService;
    private final ObjectMapper objectMapper;
    private final LlmService llmService;

    @Override
    public Label suggestLabel(Task task) {
        try {
            List<String> existingLabels = labelService.findAll().stream()
                    .map(Label::getName)
                    .toList();

            TaskSuggestLabelDto taskSuggestLabelDto = TaskSuggestLabelDto.builder()
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .priority(task.getPriority())
                    .projectName(task.getProject().getName())
                    .projectDescription(task.getProject().getDescription())
                    .sprintNames(task.getSprints().stream().map(Sprint::getName).toList())
                    .comments(task.getComments().stream().map(Comment::getText).toList())
                    .build();

            String taskInformationString = objectMapper.writeValueAsString(taskSuggestLabelDto);

            String promptTemplate = new String(Files.readAllBytes(
                    new ClassPathResource("prompts/suggest-label.txt").getFile().toPath()
            ));

            String fullPrompt = promptTemplate.replace("{{taskInformation}}", taskInformationString)
                    .replace("{{existingLabels}}", existingLabels.toString());

            String aiResponse = llmService.getResponse(fullPrompt);

            return labelService.findByNameOptional(aiResponse)
                    .orElseGet(() -> labelService.save(new Label(aiResponse)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to suggest label", e);
        }
    }
}

package org.kupchenko.projectmanagementllm;


import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kupchenko.projectmanagementllm.model.Label;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.repository.LabelRepository;
import org.kupchenko.projectmanagementllm.service.*;
import org.kupchenko.projectmanagementllm.service.impl.LabelSuggestServiceAIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Log
@SpringBootTest(classes = { ProjectManagementLlmApplication.class })
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class LabelSuggestServiceAIImplTest {
    @Autowired
    private LabelSuggestServiceAIImpl suggestService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ProjectService projectService;

    static Stream<Arguments> taskAndExpectedLabel() {
        return Stream.of(
                // 1) Уже є Documentation і Bug, а задача — “Add user profile page” → очікуємо “Feature”
                Arguments.of("Add user profile page",
                        "We need to create a new page where users can view and edit their profile",
                        "Feature"),
                // 2) Уже є Documentation і Bug, а задача — “Fix login error” → очікуємо “Bug”
                Arguments.of("Fix login error",
                        "Users report that they cannot log in under certain conditions",
                        "Bug"),
                // 3) Уже є Documentation і Bug, а задача — “Write API docs for payment” → очікуємо “Documentation”
                Arguments.of("Write API docs for payment",
                        "Generate OpenAPI specification and usage guide for payment endpoints",
                        "Documentation")
        );
    }

    @BeforeEach
    void setUp() {
        labelRepository.deleteAll();
        assertThat(labelService.findAll())
                .as("Label repository should be empty before test")
                .isEmpty();
        labelService.save(new Label("Documentation"));
        labelService.save(new Label("Bug"));
    }

    @ParameterizedTest(name = "when task title = “{0}” then suggested label = “{2}”")
    @MethodSource("taskAndExpectedLabel")
    void shouldFindOrCreateCorrectLabel(String title, String description, String expectedLabel) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(Task.Priority.MEDIUM);

        Project project = new Project();
        project.setName("Commerce Project");
        project.setDescription("Project for managing e-commerce platform");
        project = projectService.save(project);
        task.setProject(project);
        task.setSprints(List.of());
        task.setComments(List.of());

        long startMs = System.currentTimeMillis();

        Label result = suggestService.suggestLabel(task);

        long durationMs = System.currentTimeMillis() - startMs;

        assertThat(labelService.findByNameOptional(expectedLabel))
                .as("Label %s should exist", expectedLabel)
                .isPresent();

        assertThat(result.getName()).isEqualTo(expectedLabel);

        assertThat(durationMs)
                .as("Label suggesting should complete within 500 milliseconds")
                .isLessThanOrEqualTo(500);
        log.info("Label suggesting took " + durationMs + " ms");

    }
}

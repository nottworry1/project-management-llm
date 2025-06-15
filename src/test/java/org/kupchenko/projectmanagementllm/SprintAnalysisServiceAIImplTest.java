package org.kupchenko.projectmanagementllm;

import lombok.extern.java.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kupchenko.projectmanagementllm.model.Comment;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.model.TaskStatus;
import org.kupchenko.projectmanagementllm.service.impl.SprintAnalysisServiceAIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Log
@SpringBootTest(classes = {ProjectManagementLlmApplication.class})
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class SprintAnalysisServiceAIImplTest {
    @Autowired
    private SprintAnalysisServiceAIImpl analysisService;

    static Stream<Sprint> sprintProvider() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                // Sprint Alpha: 2 DONE tasks with blockers and normal comments
                buildSprint(
                        "Sprint Alpha",
                        now.minusDays(10),
                        now.minusDays(3),
                        List.of(
                                makeTask(1L, "Setup CI", "Configure pipeline", "DONE", 5,
                                        List.of(makeComment("Blocker: missing credentials"), makeComment("Retested OK"))),
                                makeTask(2L, "Write tests", "Add unit tests", "DONE", 3,
                                        List.of(makeComment("All green")))
                        )
                ),
                // Sprint Beta: 3 tasks, mix DONE/IN_PROGRESS, some without story points
                buildSprint(
                        "Sprint Beta",
                        now.minusDays(20),
                        now.minusDays(5),
                        List.of(
                                makeTask(3L, "Fix login", "Null pointer fix", "DONE", 2,
                                        List.of(makeComment("Blocker in auth module"))),
                                makeTask(4L, "Implement feature X", "New endpoint", "IN_PROGRESS", null,
                                        List.of(makeComment("Blocker: waiting on spec"))),
                                makeTask(5L, "Update docs", "API documentation", "DONE", 1,
                                        List.of(makeComment("Spellcheck done")))
                        )
                ),
                // Sprint Gamma: 4 tasks, mix statuses and multiple blockers
                buildSprint(
                        "Sprint Gamma",
                        now.minusDays(30),
                        now.minusDays(1),
                        List.of(
                                makeTask(6L, "Refactor module A", "Cleanup code", "DONE", 8,
                                        List.of(
                                                makeComment("Blocker: old dependency"),
                                                makeComment("Blocker: failing tests"),
                                                makeComment("Reviewed by QA")
                                        )
                                ),
                                makeTask(7L, "Design UI", "Mockups", "DONE", 5,
                                        List.of(makeComment("Blocker: color palette conflict")))
                                ,
                                makeTask(8L, "Integrate service B", "Service call", "IN_PROGRESS", 3,
                                        List.of())
                                ,
                                makeTask(9L, "Hotfix production", "Urgent patch", "DONE", 2,
                                        List.of(makeComment("Blocker: DB migration"), makeComment("Verified on staging")))
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("sprintProvider")
    void shouldReturnCorrectlyFormattedAnalysis(Sprint sprint) {
        long startMs = System.currentTimeMillis();

        String result = analysisService.analyzeSprint(sprint);

        long durationMs = System.currentTimeMillis() - startMs;

        assertThat(result)
                .as("Result should not be null or empty")
                .isNotNull()
                .isNotEmpty();

        String[] sentences = result.trim()
                .split("(?<=[.!?])\\s+");

        assertThat(sentences)
                .as("Expected exactly 5 sentences, but got %d", sentences.length)
                .hasSize(5);

        assertThat(durationMs)
                .as("Analysis should complete within 1000 milliseconds")
                .isLessThanOrEqualTo(1000);

        log.info("Analysis result for sprint '" + sprint.getName() + "':");
        log.info(result);
        log.info("Analysis took " + durationMs + " ms");
    }

    private static Sprint buildSprint(String name,
                                      LocalDateTime start,
                                      LocalDateTime end,
                                      List<Task> tasks) {
        Sprint sprint = new Sprint();
        sprint.setName(name);
        sprint.setStartDate(start);
        sprint.setEndDate(end);
        sprint.setTasks(tasks);
        return sprint;
    }

    private static Task makeTask(Long id,
                                 String title,
                                 String description,
                                 String status,
                                 Integer points,
                                 List<Comment> comments) {
        Task t = new Task();
        t.setId(id);
        t.setTitle(title);
        t.setDescription(description);
        TaskStatus st = new TaskStatus();
        st.setName(status);
        t.setTaskStatus(st);
        t.setStoryPoints(points);
        LocalDateTime now = LocalDateTime.now();
        t.setCreatedAt(Timestamp.valueOf(now.minusDays(2)));
        t.setStatusChangedAt(now);
        t.setComments(comments);
        return t;
    }

    private static Comment makeComment(String text) {
        Comment c = new Comment();
        c.setText(text);
        return c;
    }
}

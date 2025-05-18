package org.kupchenko.projectmanagementllm.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.kupchenko.projectmanagementllm.model.*;

import java.util.List;

@Builder
@Getter
@Setter
public class TaskSuggestLabelDto {

    private String title;

    private String description;

    private Task.Priority priority;

    private String projectName;

    private String projectDescription;

    private List<String> sprintNames;

    private List<String> comments;
}

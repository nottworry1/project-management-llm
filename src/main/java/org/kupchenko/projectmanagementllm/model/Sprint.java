package org.kupchenko.projectmanagementllm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Sprint extends AbstractEntity {
    @NotBlank
    private String name;
    @NotNull
    private String description;
    @NotNull
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne
    private Board board;

    @ManyToMany(mappedBy = "sprints")
    private List<Task> tasks;

    @ManyToOne
    private Project project;
}

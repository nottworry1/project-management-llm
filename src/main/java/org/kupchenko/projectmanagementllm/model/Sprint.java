package org.kupchenko.projectmanagementllm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Sprint extends AbstractEntity {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne
    private Board board;

    @ManyToMany(mappedBy = "sprints")
    private List<Task> tasks = new ArrayList<>();

    @ManyToOne
    private Project project;

    private Boolean closed = false;

    @Column(columnDefinition="TEXT")
    private String summary;
}

package org.kupchnko.projectmanagementllm.model;

import jakarta.persistence.*;
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
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @OneToOne
    private Board board;

    @ManyToMany(mappedBy = "sprints")
    private List<Task> tasks;

    @ManyToOne
    private Project project;
}

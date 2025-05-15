package org.kupchenko.projectmanagementllm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Board extends AbstractEntity {
    @NotBlank
    private String name;

    @NotNull
    @OneToMany(mappedBy = "board")
    private List<Sprint> sprints = new ArrayList<>();

    @OneToOne
    private Sprint currentSprint;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Project project;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<BoardTaskStatus> boardStatuses = new ArrayList<>();

    public void addSprint(Sprint sprint) {
        sprints.add(sprint);
        sprint.setBoard(this);
    }

    public void removeSprint(Sprint sprint) {
        sprints.remove(sprint);
        sprint.setBoard(null);
    }
}

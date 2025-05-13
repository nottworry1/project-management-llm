package org.kupchnko.projectmanagementllm.model;

import jakarta.persistence.*;
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
    private String name;

    @OneToOne(mappedBy = "board")
    private Sprint sprint;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<BoardTaskStatus> boardStatuses = new ArrayList<>();
}

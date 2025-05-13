package org.kupchnko.projectmanagementllm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardTaskStatus extends AbstractEntity {
    @ManyToOne
    private Board board;

    @ManyToOne
    private TaskStatus status;

    private int position;
}

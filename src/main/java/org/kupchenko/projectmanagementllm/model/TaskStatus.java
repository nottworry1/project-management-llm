package org.kupchenko.projectmanagementllm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatus extends AbstractEntity {
    @Column(unique = true, nullable = false)
    private String name;
}

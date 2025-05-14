package org.kupchenko.projectmanagementllm.model;

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
public class Comment extends AbstractEntity {
    private String text;

    @ManyToOne
    private Task task;

    @ManyToOne
    private User user;
}

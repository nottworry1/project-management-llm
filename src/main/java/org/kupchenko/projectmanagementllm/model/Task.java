package org.kupchenko.projectmanagementllm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends AbstractEntity {
    private String title;
    private String description;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User assignee;

    @ManyToOne
    private User reporter;

    @ManyToMany
    private List<Sprint> sprints;

    @ManyToOne
    private Board board;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments;

    @OneToMany(mappedBy = "task")
    private List<Attachment> attachments;

    @ManyToOne
    private TaskStatus taskStatus;
}

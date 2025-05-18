package org.kupchenko.projectmanagementllm.model;

import jakarta.persistence.*;
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

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User assignee;

    @ManyToOne
    private User reporter;

    @ManyToMany
    private List<Sprint> sprints;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments;

    @OneToMany(mappedBy = "task")
    private List<Attachment> attachments;

    @ManyToOne
    private TaskStatus taskStatus;

    @ManyToOne
    private Label label;

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
}

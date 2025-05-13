package org.kupchnko.projectmanagementllm.model;

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
public class User extends AbstractEntity
{
    private String username;

    private String password;

    private String email;

    @ManyToOne
    private Role role;

    @ManyToMany
    private List<Project> projects;

    @OneToMany(mappedBy = "assignee")
    private List<Task> assignedTasks;

    @OneToMany(mappedBy = "reporter")
    private List<Task> reportedTasks;
}

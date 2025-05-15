package org.kupchenko.projectmanagementllm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
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
public class Project extends AbstractEntity {

    @NotBlank
    private String name;
    private String description;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;

    @OneToMany(mappedBy = "project")
    private List<Sprint> sprints;

    @ManyToMany(mappedBy = "projectsMembered")
    private List<User> users;

    @ManyToMany(mappedBy = "projectsOwned")
    private List<User> owners;
}

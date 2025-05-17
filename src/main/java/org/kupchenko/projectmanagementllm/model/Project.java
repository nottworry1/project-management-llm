package org.kupchenko.projectmanagementllm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
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
public class Project extends AbstractEntity {

    @NotBlank
    private String name;
    private String description;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;

    @OneToMany(mappedBy = "project")
    private List<Sprint> sprints;

    @OneToMany(mappedBy = "project")
    private List<Board> boards;

    @ManyToMany
    private List<User> users = new ArrayList<>();

    @ManyToMany
    private List<User> owners = new ArrayList<>();

    public void addOwner(User user) {
        owners.add(user);
    }

    public void removeOwner(User user) {
        owners.remove(user);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }
}

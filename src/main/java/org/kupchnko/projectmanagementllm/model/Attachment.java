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
public class Attachment extends AbstractEntity {
    private String filePath;
    private String contentType;

    @ManyToOne
    private Task task;
}

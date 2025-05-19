package org.kupchenko.projectmanagementllm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEntity implements Comparable<AbstractEntity> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedAt;

    @Override
    public int compareTo(final AbstractEntity o) {
        if (this.id == null && o.id == null) return 0;
        if (this.id == null) return -1;
        if (o.id == null) return 1;
        return this.id.compareTo(o.id);
    }
}

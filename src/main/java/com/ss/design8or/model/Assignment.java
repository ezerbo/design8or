package com.ss.design8or.model;

import com.ss.design8or.model.enums.DesignationStatus;
import com.ss.design8or.model.enums.DesignationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author ezerbo
 *
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assignment", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"pool_id", "user_id", "assignment_date"})
})
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "assignment_date", nullable = false)
    private LocalDateTime assignmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "designation_status", columnDefinition = "VARCHAR(20)")
    private DesignationStatus designationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "designation_type", columnDefinition = "VARCHAR(20)")
    private DesignationType designationType;

    @Column(name = "designated_at")
    private LocalDateTime designatedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    public void prePersist() {
        if (assignmentDate == null) {
            assignmentDate = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment assignment = (Assignment) o;
        return Objects.equals(id, assignment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

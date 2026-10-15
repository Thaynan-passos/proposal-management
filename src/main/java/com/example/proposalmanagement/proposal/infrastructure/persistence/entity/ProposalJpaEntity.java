package com.example.proposalmanagement.proposal.infrastructure.persistence.entity;

import com.example.proposalmanagement.proposal.domain.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "proposals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProposalJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProposalStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ProposalJpaEntity fromDomain(Proposal proposal) {
        return new ProposalJpaEntity(
                proposal.getId().id(),
                proposal.getTitle(),
                proposal.getDescription().orElse(null),
                proposal.getAmount(),
                proposal.getOwner().id().id(),
                proposal.getOwner().name(),
                proposal.getStatus(),
                proposal.getCreatedAt(),
                proposal.getUpdatedAt()
        );
    }

    public Proposal toDomain() {
        return new Proposal(
                new ProposalId(this.id),
                this.title,
                this.description,
                this.amount,
                new Owner(new OwnerId(this.ownerId), this.ownerName),
                this.status,
                this.createdAt,
                this.updatedAt
        );
    }
}

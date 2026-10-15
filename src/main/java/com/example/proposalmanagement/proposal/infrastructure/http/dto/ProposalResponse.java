package com.example.proposalmanagement.proposal.infrastructure.http.dto;

import com.example.proposalmanagement.proposal.domain.Proposal;
import com.example.proposalmanagement.proposal.domain.ProposalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProposalResponse(
        UUID id,
        String title,
        String description,
        BigDecimal amount,
        OwnerResponse owner,
        ProposalStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record OwnerResponse(UUID id, String name) {}

    public static ProposalResponse from(Proposal proposal) {
        return new ProposalResponse(
                proposal.getId().id(),
                proposal.getTitle(),
                proposal.getDescription().orElse(null),
                proposal.getAmount(),
                new OwnerResponse(proposal.getOwner().id().id(), proposal.getOwner().name()),
                proposal.getStatus(),
                proposal.getCreatedAt(),
                proposal.getUpdatedAt()
        );
    }
}

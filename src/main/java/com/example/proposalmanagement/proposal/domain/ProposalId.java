package com.example.proposalmanagement.proposal.domain;

import java.util.UUID;

public record ProposalId(UUID id) {
    public ProposalId() {
        this(UUID.randomUUID());

    }
}

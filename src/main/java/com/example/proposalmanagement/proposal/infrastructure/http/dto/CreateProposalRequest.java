package com.example.proposalmanagement.proposal.infrastructure.http.dto;

import java.math.BigDecimal;

public record CreateProposalRequest(
        String title,
        String description,
        BigDecimal amount
) {
}

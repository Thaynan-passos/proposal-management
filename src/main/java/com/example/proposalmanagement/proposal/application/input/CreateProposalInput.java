package com.example.proposalmanagement.proposal.application.input;

import java.math.BigDecimal;
import java.util.Optional;

public record CreateProposalInput(
        String title,
        Optional<String> description,
        BigDecimal amount
) {
    public CreateProposalInput(String title, Optional<String> description) {
        this(title, description, BigDecimal.ZERO);
    }
}

package com.example.proposalmanagement.proposal.application;

import com.example.proposalmanagement.proposal.application.input.CreateProposalInput;
import com.example.proposalmanagement.proposal.domain.Owner;
import com.example.proposalmanagement.proposal.domain.Proposal;
import com.example.proposalmanagement.proposal.domain.ProposalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProposalUseCase {
    private final ProposalRepository proposalRepository;

    public CreateProposalUseCase(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    @Transactional
    public Proposal execute(CreateProposalInput input, Owner owner) {
        Proposal proposal = Proposal.create(
                input.title(),
                input.description().orElse(null),
                input.amount(),
                owner
        );
        return proposalRepository.save(proposal);
    }
}

package com.example.proposalmanagement.proposal.application;

import com.example.proposalmanagement.proposal.domain.OwnerId;
import com.example.proposalmanagement.proposal.domain.Proposal;
import com.example.proposalmanagement.proposal.domain.ProposalId;
import com.example.proposalmanagement.proposal.domain.ProposalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FindProposalsUseCase {
    private final ProposalRepository proposalRepository;

    public FindProposalsUseCase(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    public List<Proposal> findAll() {
        return proposalRepository.findAll();
    }

    public List<Proposal> findAllByOwnerId(OwnerId ownerId) {
        return proposalRepository.findAllByOwnerId(ownerId);
    }

    public Optional<Proposal> findById(ProposalId id) {
        return proposalRepository.findById(id);
    }
}

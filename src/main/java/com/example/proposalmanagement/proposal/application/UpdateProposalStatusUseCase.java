package com.example.proposalmanagement.proposal.application;

import com.example.proposalmanagement.proposal.domain.OwnerId;
import com.example.proposalmanagement.proposal.domain.Proposal;
import com.example.proposalmanagement.proposal.domain.ProposalId;
import com.example.proposalmanagement.proposal.domain.ProposalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
public class UpdateProposalStatusUseCase {
    private final ProposalRepository proposalRepository;

    public UpdateProposalStatusUseCase(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    public Proposal accept(ProposalId id) {
        Proposal proposal = getOrThrow(id);
        proposal.accept();
        return proposalRepository.save(proposal);
    }

    public Proposal reject(ProposalId id) {
        Proposal proposal = getOrThrow(id);
        proposal.reject();
        return proposalRepository.save(proposal);
    }

    public Proposal cancel(ProposalId id, OwnerId requestedBy) {
        Proposal proposal = getOrThrow(id);
        if (!proposal.getOwner().id().equals(requestedBy)) {
            throw new IllegalStateException("Apenas o proprietário pode cancelar a proposta");
        }
        proposal.cancel();
        return proposalRepository.save(proposal);
    }

    private Proposal getOrThrow(ProposalId id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proposta não encontrada com o ID: " + id.id()));
    }
}

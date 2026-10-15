package com.example.proposalmanagement.proposal.domain;

import java.util.List;
import java.util.Optional;

public interface ProposalRepository {
    List<Proposal> findAll();
    List<Proposal> findAllByOwnerId(OwnerId ownerId);
    Optional<Proposal> findById(ProposalId id);
    Proposal save(Proposal proposal);
    void deleteById(ProposalId id);
}

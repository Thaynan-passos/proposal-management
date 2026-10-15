package com.example.proposalmanagement.proposal.infrastructure.persistence;

import com.example.proposalmanagement.proposal.domain.OwnerId;
import com.example.proposalmanagement.proposal.domain.Proposal;
import com.example.proposalmanagement.proposal.domain.ProposalId;
import com.example.proposalmanagement.proposal.domain.ProposalRepository;
import com.example.proposalmanagement.proposal.infrastructure.persistence.entity.ProposalJpaEntity;
import com.example.proposalmanagement.proposal.infrastructure.persistence.repository.SpringDataProposalRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProposalRepositoryImpl implements ProposalRepository {

    private final SpringDataProposalRepository springDataProposalRepository;

    public ProposalRepositoryImpl(SpringDataProposalRepository springDataProposalRepository) {
        this.springDataProposalRepository = springDataProposalRepository;
    }

    @Override
    public List<Proposal> findAll() {
        return springDataProposalRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ProposalJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Proposal> findAllByOwnerId(OwnerId ownerId) {
        return springDataProposalRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId.id())
                .stream()
                .map(ProposalJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Proposal> findById(ProposalId id) {
        return springDataProposalRepository.findById(id.id())
                .map(ProposalJpaEntity::toDomain);
    }

    @Override
    public Proposal save(Proposal proposal) {
        ProposalJpaEntity entity = ProposalJpaEntity.fromDomain(proposal);
        ProposalJpaEntity saved = springDataProposalRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public void deleteById(ProposalId id) {
        springDataProposalRepository.deleteById(id.id());
    }
}

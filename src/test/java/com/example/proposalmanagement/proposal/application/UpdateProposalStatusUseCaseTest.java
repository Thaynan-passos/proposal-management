package com.example.proposalmanagement.proposal.application;

import com.example.proposalmanagement.proposal.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProposalStatusUseCaseTest {

    @Mock
    private ProposalRepository proposalRepository;

    private UpdateProposalStatusUseCase useCase;

    private Owner owner;
    private Proposal proposal;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProposalStatusUseCase(proposalRepository);
        owner = new Owner(new OwnerId(UUID.randomUUID()), "fitness_vibe");
        proposal = Proposal.create("Campanha Fitness", "Descrição", new BigDecimal("1000.00"), owner);
    }

    @Test
    @DisplayName("Deve aceitar proposta com sucesso")
    void shouldAcceptProposal() {
        when(proposalRepository.findById(proposal.getId())).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Proposal result = useCase.accept(proposal.getId());

        assertEquals(ProposalStatus.ACCEPTED, result.getStatus());
        verify(proposalRepository).save(proposal);
    }

    @Test
    @DisplayName("Deve rejeitar proposta com sucesso")
    void shouldRejectProposal() {
        when(proposalRepository.findById(proposal.getId())).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Proposal result = useCase.reject(proposal.getId());

        assertEquals(ProposalStatus.REJECTED, result.getStatus());
        verify(proposalRepository).save(proposal);
    }

    @Test
    @DisplayName("Deve cancelar proposta quando solicitado pelo proprietário")
    void shouldCancelProposalByOwner() {
        when(proposalRepository.findById(proposal.getId())).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Proposal result = useCase.cancel(proposal.getId(), owner.id());

        assertEquals(ProposalStatus.CANCELLED, result.getStatus());
        verify(proposalRepository).save(proposal);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cancelar proposta pertencente a outro usuário")
    void shouldThrowExceptionWhenCancellingByNonOwner() {
        OwnerId anotherOwnerId = new OwnerId(UUID.randomUUID());
        when(proposalRepository.findById(proposal.getId())).thenReturn(Optional.of(proposal));

        assertThrows(IllegalStateException.class, () -> useCase.cancel(proposal.getId(), anotherOwnerId));
        verify(proposalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar NoSuchElementException se a proposta não existir")
    void shouldThrowWhenProposalNotFound() {
        ProposalId nonexistentId = new ProposalId(UUID.randomUUID());
        when(proposalRepository.findById(nonexistentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> useCase.accept(nonexistentId));
    }
}

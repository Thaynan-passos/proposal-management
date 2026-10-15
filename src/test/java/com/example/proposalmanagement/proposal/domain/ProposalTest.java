package com.example.proposalmanagement.proposal.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProposalTest {

    private final Owner testOwner = new Owner(new OwnerId(UUID.randomUUID()), "fitness_vibe");

    @Test
    @DisplayName("Deve criar uma proposta com status PENDING por padrão")
    void shouldCreateProposalWithPendingStatus() {
        Proposal proposal = Proposal.create(
                "Campanha Verão 2026",
                "Divulgação de suplementos alimentares",
                new BigDecimal("1500.00"),
                testOwner
        );

        assertNotNull(proposal.getId());
        assertEquals("Campanha Verão 2026", proposal.getTitle());
        assertTrue(proposal.getDescription().isPresent());
        assertEquals("Divulgação de suplementos alimentares", proposal.getDescription().get());
        assertEquals(new BigDecimal("1500.00"), proposal.getAmount());
        assertEquals(testOwner, proposal.getOwner());
        assertEquals(ProposalStatus.PENDING, proposal.getStatus());
        assertNotNull(proposal.getCreatedAt());
        assertNotNull(proposal.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve lançar exceção se o título for nulo ou vazio")
    void shouldThrowExceptionWhenTitleIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                Proposal.create("", "Descrição", BigDecimal.TEN, testOwner)
        );
        assertThrows(IllegalArgumentException.class, () ->
                Proposal.create(null, "Descrição", BigDecimal.TEN, testOwner)
        );
    }

    @Test
    @DisplayName("Deve aceitar proposta pendente com sucesso")
    void shouldAcceptPendingProposal() {
        Proposal proposal = Proposal.create("Título", "Desc", BigDecimal.TEN, testOwner);
        proposal.accept();

        assertEquals(ProposalStatus.ACCEPTED, proposal.getStatus());
    }

    @Test
    @DisplayName("Deve rejeitar proposta pendente com sucesso")
    void shouldRejectPendingProposal() {
        Proposal proposal = Proposal.create("Título", "Desc", BigDecimal.TEN, testOwner);
        proposal.reject();

        assertEquals(ProposalStatus.REJECTED, proposal.getStatus());
    }

    @Test
    @DisplayName("Não deve aceitar proposta já cancelada ou rejeitada")
    void shouldNotAcceptNonPendingProposal() {
        Proposal proposal = Proposal.create("Título", "Desc", BigDecimal.TEN, testOwner);
        proposal.reject();

        assertThrows(IllegalStateException.class, proposal::accept);
    }

    @Test
    @DisplayName("Deve cancelar proposta pendente")
    void shouldCancelPendingProposal() {
        Proposal proposal = Proposal.create("Título", "Desc", BigDecimal.TEN, testOwner);
        proposal.cancel();

        assertEquals(ProposalStatus.CANCELLED, proposal.getStatus());
    }

    @Test
    @DisplayName("Não deve permitir cancelar proposta aceita")
    void shouldNotCancelAcceptedProposal() {
        Proposal proposal = Proposal.create("Título", "Desc", BigDecimal.TEN, testOwner);
        proposal.accept();

        assertThrows(IllegalStateException.class, proposal::cancel);
    }
}

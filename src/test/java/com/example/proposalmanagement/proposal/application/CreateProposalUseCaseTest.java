package com.example.proposalmanagement.proposal.application;

import com.example.proposalmanagement.proposal.application.input.CreateProposalInput;
import com.example.proposalmanagement.proposal.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProposalUseCaseTest {

    @Mock
    private ProposalRepository proposalRepository;

    private CreateProposalUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateProposalUseCase(proposalRepository);
    }

    @Test
    @DisplayName("Deve criar e salvar proposta com sucesso")
    void shouldCreateAndSaveProposal() {
        Owner owner = new Owner(new OwnerId(UUID.randomUUID()), "tech_guru");
        CreateProposalInput input = new CreateProposalInput(
                "Review de Smartphone",
                Optional.of("Vídeo de unboxing e benchmark"),
                new BigDecimal("2500.00")
        );

        when(proposalRepository.save(any(Proposal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Proposal result = useCase.execute(input, owner);

        assertNotNull(result);
        assertEquals("Review de Smartphone", result.getTitle());
        assertEquals("Vídeo de unboxing e benchmark", result.getDescription().orElse(""));
        assertEquals(new BigDecimal("2500.00"), result.getAmount());
        assertEquals(owner, result.getOwner());
        verify(proposalRepository, times(1)).save(any(Proposal.class));
    }
}

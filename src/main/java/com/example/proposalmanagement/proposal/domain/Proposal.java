package com.example.proposalmanagement.proposal.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Getter
public class Proposal {
    private final ProposalId id;
    private String title;
    private String description;
    private BigDecimal amount;
    private final Owner owner;
    private ProposalStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Proposal(ProposalId id,
                    String title,
                    String description,
                    BigDecimal amount,
                    Owner owner,
                    ProposalStatus status,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("O id da proposta é obrigatório");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("O título da proposta é obrigatório");
        }
        if (owner == null) {
            throw new IllegalArgumentException("O proprietário da proposta é obrigatório");
        }

        this.id = id;
        this.title = title.trim();
        this.description = description != null ? description.trim() : null;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.owner = owner;
        this.status = status != null ? status : ProposalStatus.PENDING;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public static Proposal create(String title, String description, BigDecimal amount, Owner owner) {
        return new Proposal(
                new ProposalId(),
                title,
                description,
                amount,
                owner,
                ProposalStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void update(String newTitle, String newDescription, BigDecimal newAmount) {
        if (status == ProposalStatus.ACCEPTED || status == ProposalStatus.CANCELLED) {
            throw new IllegalStateException("Não é possível alterar uma proposta finalizada (" + status + ")");
        }
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser vazio");
        }
        this.title = newTitle.trim();
        this.description = newDescription != null ? newDescription.trim() : null;
        if (newAmount != null) {
            this.amount = newAmount;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void accept() {
        if (this.status != ProposalStatus.PENDING) {
            throw new IllegalStateException("Apenas propostas pendentes podem ser aceitas. Status atual: " + status);
        }
        this.status = ProposalStatus.ACCEPTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != ProposalStatus.PENDING) {
            throw new IllegalStateException("Apenas propostas pendentes podem ser rejeitadas. Status atual: " + status);
        }
        this.status = ProposalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == ProposalStatus.ACCEPTED) {
            throw new IllegalStateException("Não é possível cancelar uma proposta já aceita.");
        }
        if (this.status == ProposalStatus.CANCELLED) {
            return;
        }
        this.status = ProposalStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposal proposal = (Proposal) o;
        return Objects.equals(id, proposal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package com.example.proposalmanagement.proposal.infrastructure.http;

import com.example.proposalmanagement.auth.domain.UserRole;
import com.example.proposalmanagement.auth.infrastructure.persistence.entity.User;
import com.example.proposalmanagement.proposal.application.CreateProposalUseCase;
import com.example.proposalmanagement.proposal.application.FindProposalsUseCase;
import com.example.proposalmanagement.proposal.application.UpdateProposalStatusUseCase;
import com.example.proposalmanagement.proposal.application.input.CreateProposalInput;
import com.example.proposalmanagement.proposal.domain.Owner;
import com.example.proposalmanagement.proposal.domain.OwnerId;
import com.example.proposalmanagement.proposal.domain.Proposal;
import com.example.proposalmanagement.proposal.domain.ProposalId;
import com.example.proposalmanagement.proposal.infrastructure.http.dto.CreateProposalRequest;
import com.example.proposalmanagement.proposal.infrastructure.http.dto.ProposalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/proposals")
public class ProposalController {

    private final CreateProposalUseCase createProposalUseCase;
    private final FindProposalsUseCase findProposalsUseCase;
    private final UpdateProposalStatusUseCase updateProposalStatusUseCase;

    public ProposalController(CreateProposalUseCase createProposalUseCase,
                              FindProposalsUseCase findProposalsUseCase,
                              UpdateProposalStatusUseCase updateProposalStatusUseCase) {
        this.createProposalUseCase = createProposalUseCase;
        this.findProposalsUseCase = findProposalsUseCase;
        this.updateProposalStatusUseCase = updateProposalStatusUseCase;
    }

    @PostMapping
    public ResponseEntity<ProposalResponse> createProposal(
            @RequestBody CreateProposalRequest request,
            @AuthenticationPrincipal User currentUser) {

        Owner owner = new Owner(new OwnerId(currentUser.getId()), currentUser.getUsername());
        CreateProposalInput input = new CreateProposalInput(
                request.title(),
                Optional.ofNullable(request.description()),
                request.amount()
        );

        Proposal created = createProposalUseCase.execute(input, owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProposalResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<ProposalResponse>> listProposals(
            @RequestParam(required = false) Boolean mine,
            @AuthenticationPrincipal User currentUser) {

        List<Proposal> proposals;
        boolean filterByOwner = Boolean.TRUE.equals(mine) ||
                (mine == null && currentUser.getRole() == UserRole.INFLUENCER);

        if (filterByOwner) {
            proposals = findProposalsUseCase.findAllByOwnerId(new OwnerId(currentUser.getId()));
        } else {
            proposals = findProposalsUseCase.findAll();
        }

        List<ProposalResponse> response = proposals.stream()
                .map(ProposalResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalResponse> getProposalById(@PathVariable UUID id) {
        return findProposalsUseCase.findById(new ProposalId(id))
                .map(proposal -> ResponseEntity.ok(ProposalResponse.from(proposal)))
                .orElseThrow(() -> new NoSuchElementException("Proposta não encontrada: " + id));
    }

    @PatchMapping("/{id}/accept")
    @PreAuthorize("hasRole('BRAND')")
    public ResponseEntity<ProposalResponse> acceptProposal(@PathVariable UUID id) {
        Proposal updated = updateProposalStatusUseCase.accept(new ProposalId(id));
        return ResponseEntity.ok(ProposalResponse.from(updated));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('BRAND')")
    public ResponseEntity<ProposalResponse> rejectProposal(@PathVariable UUID id) {
        Proposal updated = updateProposalStatusUseCase.reject(new ProposalId(id));
        return ResponseEntity.ok(ProposalResponse.from(updated));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ProposalResponse> cancelProposal(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        Proposal updated = updateProposalStatusUseCase.cancel(
                new ProposalId(id),
                new OwnerId(currentUser.getId())
        );
        return ResponseEntity.ok(ProposalResponse.from(updated));
    }
}

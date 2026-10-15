package com.example.proposalmanagement.proposal.infrastructure.http;

import com.example.proposalmanagement.auth.domain.UserRole;
import com.example.proposalmanagement.auth.infrastructure.persistence.entity.User;
import com.example.proposalmanagement.auth.infrastructure.persistence.repository.UserRepository;
import com.example.proposalmanagement.proposal.domain.ProposalStatus;
import com.example.proposalmanagement.proposal.infrastructure.persistence.entity.ProposalJpaEntity;
import com.example.proposalmanagement.proposal.infrastructure.persistence.repository.SpringDataProposalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProposalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpringDataProposalRepository proposalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User influencerUser;
    private User brandUser;

    @BeforeEach
    void setUp() {
        proposalRepository.deleteAll();

        influencerUser = userRepository.findByUsername("fitness_vibe").orElseGet(() -> {
            User u = new User();
            u.setUsername("fitness_vibe");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRole(UserRole.INFLUENCER);
            return userRepository.save(u);
        });

        brandUser = userRepository.findByUsername("logistics").orElseGet(() -> {
            User u = new User();
            u.setUsername("logistics");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRole(UserRole.BRAND);
            return userRepository.save(u);
        });
    }

    @Test
    @DisplayName("Requisição não autenticada deve retornar 401 Unauthorized")
    void unauthenticatedRequestShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/proposals"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Influencer autenticado consegue acessar endpoint /influencer e criar proposta")
    @WithUserDetails("fitness_vibe")
    void influencerCanAccessInfluencerEndpointAndCreateProposal() throws Exception {
        mockMvc.perform(get("/influencer"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello influencer!"));

        String newProposalJson = """
                {
                    "title": "Post no Instagram",
                    "description": "Divulgação de linha fitness com 3 stories e 1 reel",
                    "amount": 1200.00
                }
                """;

        mockMvc.perform(post("/api/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newProposalJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Post no Instagram"))
                .andExpect(jsonPath("$.description").value("Divulgação de linha fitness com 3 stories e 1 reel"))
                .andExpect(jsonPath("$.amount").value(1200.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.owner.name").value("fitness_vibe"));
    }

    @Test
    @DisplayName("Influencer não tem acesso ao endpoint /brand (403 Forbidden)")
    @WithUserDetails("fitness_vibe")
    void influencerCannotAccessBrandEndpoint() throws Exception {
        mockMvc.perform(get("/brand"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Brand autenticada consegue acessar endpoint /brand e aceitar proposta")
    @WithUserDetails("logistics")
    void brandCanAccessBrandEndpointAndAcceptProposal() throws Exception {
        mockMvc.perform(get("/brand"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello brand!"));

        ProposalJpaEntity proposalEntity = new ProposalJpaEntity(
                UUID.randomUUID(),
                "Campanha de Inverno",
                "Divulgação de casacos térmicos",
                new BigDecimal("3000.00"),
                influencerUser.getId(),
                influencerUser.getUsername(),
                ProposalStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        proposalRepository.save(proposalEntity);

        mockMvc.perform(patch("/api/proposals/" + proposalEntity.getId() + "/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("Influencer não pode aceitar proposta (apenas BRAND)")
    @WithUserDetails("fitness_vibe")
    void influencerCannotAcceptProposal() throws Exception {
        ProposalJpaEntity proposalEntity = new ProposalJpaEntity(
                UUID.randomUUID(),
                "Campanha Fitness",
                "Suplementos",
                new BigDecimal("800.00"),
                influencerUser.getId(),
                influencerUser.getUsername(),
                ProposalStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        proposalRepository.save(proposalEntity);

        mockMvc.perform(patch("/api/proposals/" + proposalEntity.getId() + "/accept"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Endpoint /api/auth/me deve retornar dados do usuário logado")
    @WithUserDetails("fitness_vibe")
    void shouldReturnCurrentUserProfile() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("fitness_vibe"))
                .andExpect(jsonPath("$.role").value("INFLUENCER"));
    }
}

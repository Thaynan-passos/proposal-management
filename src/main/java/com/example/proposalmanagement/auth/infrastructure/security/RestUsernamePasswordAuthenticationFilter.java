package com.example.proposalmanagement.auth.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * Filtro de login que recebe as credenciais em JSON (POST /api/auth/login).
 * Não é um @Component: é instanciado na SecurityConfig para não ser registrado duas vezes.
 */
public class RestUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String LOGIN_URL = "/api/auth/login";

    private final JsonMapper jsonMapper;

    public RestUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                    JsonMapper jsonMapper) {
        super(authenticationManager);
        this.jsonMapper = jsonMapper;

        setRequiresAuthenticationRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, LOGIN_URL));
        setSecurityContextRepository(new HttpSessionSecurityContextRepository());
        setSessionAuthenticationStrategy(new ChangeSessionIdAuthenticationStrategy());

        setAuthenticationSuccessHandler((request, response, authentication) ->
                response.setStatus(HttpServletResponse.SC_OK));
        setAuthenticationFailureHandler((request, response, exception) ->
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            var loginRequest = jsonMapper.readValue(request.getInputStream(), LoginRequest.class);

            if (loginRequest == null || loginRequest.username() == null || loginRequest.password() == null) {
                throw new BadCredentialsException("Usuário e senha são obrigatórios");
            }

            var token = UsernamePasswordAuthenticationToken.unauthenticated(
                    loginRequest.username(),
                    loginRequest.password());

            return getAuthenticationManager().authenticate(token);

        } catch (IOException | JacksonException e) {
            throw new AuthenticationServiceException("Erro ao ler credenciais do corpo da requisição", e);
        }
    }

    public record LoginRequest(String username, String password) {
    }
}

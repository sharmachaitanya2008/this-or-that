package com.designduel.service;

import com.designduel.dto.LoginRequest;
import com.designduel.dto.LoginResponse;
import com.designduel.model.Judge;
import com.designduel.repository.JudgeRepository;
import com.designduel.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final JudgeRepository judgeRepository;
    private final JwtTokenProvider tokenProvider;
    private final SessionService sessionService;

    @Value("${app.username-validation.enabled:false}")
    private boolean validationEnabled;

    @Value("${app.username-validation.pattern:^[a-z0-9._-]+$}")
    private String validationPattern;

    public LoginResponse login(LoginRequest request) {
        String raw = request.getUsername().trim();
        String normalized = raw.toLowerCase();

        if (validationEnabled) {
            if (!Pattern.matches(validationPattern, normalized)) {
                log.warn("Username '{}' failed validation pattern '{}'", normalized, validationPattern);
                throw new IllegalArgumentException("Username must match pattern: " + validationPattern);
            }
        }

        Optional<Judge> existing = judgeRepository.findByUsernameIgnoreCase(normalized);
        Judge judge;

        if (existing.isPresent()) {
            judge = existing.get();
            judge.setLastActive(Instant.now());
            judge.setActive(true);
            log.info("Judge '{}' ({}) logged in", normalized, judge.getId());
        } else {
            String judgeId = "judge" + UUID.randomUUID().toString().substring(0, 8);
            judge = Judge.builder().username(normalized).build();
            judge.setId(judgeId);
            log.info("New judge '{}' created with id {}", normalized, judgeId);
        }

        judgeRepository.save(judge);

        sessionService.initializeSession(judge.getId());

        String token = tokenProvider.generateToken(judge.getId(), judge.getUsername());

        return new LoginResponse(judge.getId(), judge.getUsername(), token, tokenProvider.getExpirationMs() / 1000);
    }
}

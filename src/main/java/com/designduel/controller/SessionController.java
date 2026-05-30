package com.designduel.controller;

import com.designduel.dto.SessionResponse;
import com.designduel.model.Design;
import com.designduel.model.JudgeSession;
import com.designduel.service.DesignService;
import com.designduel.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session")
@AllArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final DesignService designService;

    @GetMapping("/current")
    public ResponseEntity<SessionResponse> getCurrentSession(@AuthenticationPrincipal String judgeId) {
        JudgeSession session = sessionService.getSession(judgeId);

        Design champion = designService.getById(session.getCurrentChampionId());
        Design challenger = session.getCurrentChallengerId() != null
                ? designService.getById(session.getCurrentChallengerId())
                : null;

        SessionResponse response = new SessionResponse(
                session.getId(),
                session.getJudgeId(),
                champion,
                challenger,
                session.getVotesCast(),
                session.getProgressPercentage(),
                session.isCompleted()
        );

        return ResponseEntity.ok(response);
    }
}

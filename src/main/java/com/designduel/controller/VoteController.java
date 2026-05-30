package com.designduel.controller;

import com.designduel.dto.VoteRequest;
import com.designduel.dto.VoteResult;
import com.designduel.service.VoteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitVote(
            @AuthenticationPrincipal String judgeId,
            @Valid @RequestBody VoteRequest request) {
        VoteResult result = voteService.submitVote(judgeId, request);
        Map<String, Object> body = new HashMap<>();
        body.put("winner", result.getWinner());
        body.put("challenger", result.getChallenger());
        body.put("completed", result.isCompleted());
        body.put("votesCast", result.getVotesCast());
        body.put("progressPercentage", result.getProgressPercentage());
        body.put("message", "Vote recorded successfully");
        return ResponseEntity.ok(body);
    }
}

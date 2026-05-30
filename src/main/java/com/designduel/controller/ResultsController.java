package com.designduel.controller;

import com.designduel.dto.DesignRanking;
import com.designduel.dto.ResultsResponse;
import com.designduel.model.Design;
import com.designduel.model.TournamentResult;
import com.designduel.service.ResultsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@AllArgsConstructor
public class ResultsController {

    private final ResultsService resultsService;

    @GetMapping
    public ResponseEntity<ResultsResponse> getResults(@AuthenticationPrincipal String judgeId) {
        List<DesignRanking> rankings = resultsService.getRankings();
        DesignRanking winner = resultsService.getWinner();
        Design myFinalPick = resultsService.getMyFinalPick(judgeId);
        return ResponseEntity.ok(new ResultsResponse(rankings, winner, myFinalPick));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TournamentResult>> getHistory() {
        return ResponseEntity.ok(resultsService.getHistory());
    }

    @PostMapping("/finalize")
    public ResponseEntity<TournamentResult> finalizeResults() {
        TournamentResult result = resultsService.persistWinner();
        if (result == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }
}

package com.designduel.controller;

import com.designduel.dto.ProgressResponse;
import com.designduel.model.Judge;
import com.designduel.service.JudgeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/judges")
@AllArgsConstructor
public class JudgeController {

    private final JudgeService judgeService;

    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getProgress(@AuthenticationPrincipal String judgeId) {
        return ResponseEntity.ok(judgeService.getProgress(judgeId));
    }

    @GetMapping("/me")
    public ResponseEntity<Judge> getCurrentJudge(@AuthenticationPrincipal String judgeId) {
        return ResponseEntity.ok(judgeService.getJudge(judgeId));
    }

    @GetMapping
    public ResponseEntity<Page<Judge>> getAllJudges(@PageableDefault(size = 20, sort = "username") Pageable pageable) {
        return ResponseEntity.ok(judgeService.getAllJudges(pageable));
    }
}

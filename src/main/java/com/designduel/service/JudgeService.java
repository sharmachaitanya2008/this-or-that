package com.designduel.service;

import com.designduel.dto.ProgressResponse;
import com.designduel.model.Judge;
import com.designduel.model.JudgeSession;
import com.designduel.repository.JudgeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class JudgeService {

    private final JudgeRepository judgeRepository;
    private final SessionService sessionService;

    public ProgressResponse getProgress(String judgeId) {
        JudgeSession session = sessionService.getSession(judgeId);
        return new ProgressResponse(
                session.getVotesCast(),
                session.getProgressPercentage(),
                session.isCompleted()
        );
    }

    public Judge getJudge(String judgeId) {
        return judgeRepository.findById(judgeId)
                .orElseThrow(() -> new RuntimeException("Judge not found: " + judgeId));
    }

    public Page<Judge> getAllJudges(Pageable pageable) {
        return judgeRepository.findAll(pageable);
    }
}

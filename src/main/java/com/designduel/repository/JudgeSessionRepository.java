package com.designduel.repository;

import com.designduel.model.JudgeSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface JudgeSessionRepository extends MongoRepository<JudgeSession, String> {
    Optional<JudgeSession> findByJudgeId(String judgeId);
}

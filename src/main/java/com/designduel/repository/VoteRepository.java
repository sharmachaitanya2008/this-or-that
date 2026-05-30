package com.designduel.repository;

import com.designduel.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface VoteRepository extends MongoRepository<Vote, String> {
    List<Vote> findByJudgeId(String judgeId);
    int countByJudgeId(String judgeId);
    List<Vote> findBySessionId(String sessionId);
}

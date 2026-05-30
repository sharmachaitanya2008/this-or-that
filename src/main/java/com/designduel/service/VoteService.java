package com.designduel.service;

import com.designduel.dto.VoteRequest;
import com.designduel.dto.VoteResult;
import com.designduel.model.Design;
import com.designduel.model.Judge;
import com.designduel.model.JudgeSession;
import com.designduel.model.Vote;
import com.designduel.repository.DesignRepository;
import com.designduel.repository.JudgeRepository;
import com.designduel.repository.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final DesignRepository designRepository;
    private final JudgeRepository judgeRepository;
    private final SessionService sessionService;
    private final DesignService designService;
    private final MongoTemplate mongoTemplate;
    private final CacheService cacheService;

    public VoteResult submitVote(String judgeId, VoteRequest request) {

        Design winner = designRepository.findById(request.getWinnerId())
                .orElseThrow(() -> new RuntimeException("Design not found: " + request.getWinnerId()));
        Design loser = designRepository.findById(request.getLoserId())
                .orElseThrow(() -> new RuntimeException("Design not found: " + request.getLoserId()));

        JudgeSession session = sessionService.getSession(judgeId);

        Vote vote = Vote.builder()
                .judgeId(judgeId)
                .winnerId(winner.getId())
                .loserId(loser.getId())
                .sessionId(session.getId())
                .build();
        voteRepository.save(vote);

        log.debug("Vote recorded: judge={} winner={} loser={} session={}", judgeId, winner.getId(), loser.getId(), session.getId());

        atomicIncrementDesignCounters(winner.getId(), "wins", 1);
        atomicIncrementDesignCounters(loser.getId(), "losses", 1);
        atomicIncrementDesignCounters(winner.getId(), "comparisons", 1);
        atomicIncrementDesignCounters(loser.getId(), "comparisons", 1);
        atomicIncrementDesignCounters(winner.getId(), "appearances", 1);
        atomicIncrementDesignCounters(loser.getId(), "appearances", 1);

        sessionService.incrementVotesCast(judgeId);

        atomicIncrementJudgeVotes(judgeId);

        sessionService.updateChampion(judgeId, winner.getId());

        Design challenger = designService.selectChallenger(judgeId);

        if (challenger == null) {
            completeJudgeSession(judgeId, winner);
            session = sessionService.getSession(judgeId);
            return new VoteResult(winner, null, true, session.getVotesCast(), session.getProgressPercentage());
        }

        sessionService.setChallenger(judgeId, challenger.getId());

        session = sessionService.getSession(judgeId);
        if (session.getDesignsSeen().size() >= cacheService.getActiveDesignCount()) {
            completeJudgeSession(judgeId, winner);
            return new VoteResult(winner, null, true, session.getVotesCast(), session.getProgressPercentage());
        }

        return new VoteResult(winner, challenger, false, session.getVotesCast(), session.getProgressPercentage());
    }

    private void atomicIncrementDesignCounters(String designId, String field, int amount) {
        Query query = Query.query(Criteria.where("id").is(designId));
        Update update = new Update().inc(field, amount);
        mongoTemplate.updateFirst(query, update, Design.class);
    }

    private void atomicIncrementJudgeVotes(String judgeId) {
        Query query = Query.query(Criteria.where("id").is(judgeId));
        Update update = new Update().inc("totalVotesCast", 1);
        mongoTemplate.updateFirst(query, update, Judge.class);
    }

    private void completeJudgeSession(String judgeId, Design winner) {
        sessionService.markCompleted(judgeId);
        Query query = Query.query(Criteria.where("id").is(judgeId));
        Update update = new Update()
                .set("completed", true)
                .set("finalPickId", winner.getId())
                .set("finalPickTitle", winner.getTitle());
        mongoTemplate.updateFirst(query, update, Judge.class);
        log.info("Judge {} completed. Final pick: {} ({})", judgeId, winner.getTitle(), winner.getId());
    }
}

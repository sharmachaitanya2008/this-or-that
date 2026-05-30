package com.designduel.service;

import com.designduel.model.Design;
import com.designduel.model.JudgeSession;
import com.designduel.repository.DesignRepository;
import com.designduel.repository.JudgeSessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class SessionService {

    private final JudgeSessionRepository sessionRepository;
    private final DesignRepository designRepository;
    private final MongoTemplate mongoTemplate;
    private final CacheService cacheService;

    public JudgeSession initializeSession(String judgeId) {
        Optional<JudgeSession> existing = sessionRepository.findByJudgeId(judgeId);

        if (existing.isPresent()) {
            JudgeSession session = existing.get();
            session.setLastActive(Instant.now());
            return sessionRepository.save(session);
        }

        JudgeSession session = JudgeSession.builder().judgeId(judgeId).build();
        session.setId("session" + UUID.randomUUID().toString().substring(0, 8));

        List<Design> activeDesigns = designRepository.findByActiveTrue();

        if (activeDesigns.size() >= 2) {
            Collections.shuffle(activeDesigns);
            session.setCurrentChampionId(activeDesigns.get(0).getId());
            session.setCurrentChallengerId(activeDesigns.get(1).getId());
            session.getDesignsSeen().add(activeDesigns.get(0).getId());
            session.getDesignsSeen().add(activeDesigns.get(1).getId());
            session.getRecentlyShown().add(activeDesigns.get(0).getId());
            session.getRecentlyShown().add(activeDesigns.get(1).getId());
            updateProgress(session);
        }

        session.setLastActive(Instant.now());
        return sessionRepository.save(session);
    }

    public JudgeSession getSession(String judgeId) {
        return sessionRepository.findByJudgeId(judgeId)
                .orElseThrow(() -> new RuntimeException("Session not found for judge: " + judgeId));
    }

    public void updateChampion(String judgeId, String newChampionId) {
        Query query = Query.query(Criteria.where("judgeId").is(judgeId));
        Update update = new Update()
                .set("currentChampionId", newChampionId)
                .set("lastActive", Instant.now())
                .addToSet("designsSeen", newChampionId);

        mongoTemplate.updateFirst(query, update, JudgeSession.class);
        updateRecentlyShown(judgeId, newChampionId);
        recalculateProgress(judgeId);
    }

    public void setChallenger(String judgeId, String challengerId) {
        Query query = Query.query(Criteria.where("judgeId").is(judgeId));
        Update update = new Update()
                .set("currentChallengerId", challengerId)
                .set("lastActive", Instant.now())
                .addToSet("designsSeen", challengerId);

        mongoTemplate.updateFirst(query, update, JudgeSession.class);
        updateRecentlyShown(judgeId, challengerId);
        recalculateProgress(judgeId);
    }

    private void updateRecentlyShown(String judgeId, String designId) {
        JudgeSession session = getSession(judgeId);
        session.getRecentlyShown().add(designId);
        if (session.getRecentlyShown().size() > 10) {
            session.getRecentlyShown().remove(0);
        }
        Query query = Query.query(Criteria.where("judgeId").is(judgeId));
        Update update = new Update().set("recentlyShown", session.getRecentlyShown());
        mongoTemplate.updateFirst(query, update, JudgeSession.class);
    }

    public void incrementVotesCast(String judgeId) {
        Query query = Query.query(Criteria.where("judgeId").is(judgeId));
        Update update = new Update()
                .inc("votesCast", 1)
                .set("lastActive", Instant.now());
        mongoTemplate.updateFirst(query, update, JudgeSession.class);
    }

    public void markCompleted(String judgeId) {
        Query query = Query.query(Criteria.where("judgeId").is(judgeId));
        Update update = new Update()
                .set("completed", true)
                .set("progressPercentage", 100.0)
                .set("lastActive", Instant.now());
        mongoTemplate.updateFirst(query, update, JudgeSession.class);
    }

    private void updateProgress(JudgeSession session) {
        long totalDesigns = cacheService.getActiveDesignCount();
        if (totalDesigns == 0) {
            session.setProgressPercentage(0);
            return;
        }
        double seen = session.getDesignsSeen().size();
        double progress = (seen / totalDesigns) * 100;
        session.setProgressPercentage(Math.min(progress, 100));
    }

    private void recalculateProgress(String judgeId) {
        JudgeSession session = getSession(judgeId);
        updateProgress(session);
        Query query = Query.query(Criteria.where("judgeId").is(judgeId));
        Update update = new Update().set("progressPercentage", session.getProgressPercentage());
        mongoTemplate.updateFirst(query, update, JudgeSession.class);
    }

    public List<Design> getSeenDesigns(String judgeId) {
        JudgeSession session = getSession(judgeId);
        List<String> seenIds = session.getDesignsSeen();
        return designRepository.findAllById(seenIds);
    }
}

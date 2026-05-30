package com.designduel.service;

import com.designduel.model.Design;
import com.designduel.model.JudgeSession;
import com.designduel.repository.DesignRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class DesignService {

    private final DesignRepository designRepository;
    private final SessionService sessionService;
    private final MongoTemplate mongoTemplate;
    private final CacheService cacheService;

    public Design getById(String id) {
        return designRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Design not found: " + id));
    }

    public List<Design> getAllActive() {
        return designRepository.findByActiveTrue();
    }

    public Page<Design> getAllActive(Pageable pageable) {
        return designRepository.findByActiveTrue(pageable);
    }

    public Map<String, Design> getDuel(String judgeId) {
        JudgeSession session = sessionService.getSession(judgeId);
        Design champion = getById(session.getCurrentChampionId());
        Design challenger = getById(session.getCurrentChallengerId());

        Map<String, Design> duel = new LinkedHashMap<>();
        duel.put("champion", champion);
        duel.put("challenger", challenger);
        return duel;
    }

    public Design selectChallenger(String judgeId) {
        JudgeSession session = sessionService.getSession(judgeId);

        String championId = session.getCurrentChampionId();
        List<String> recentlyShown = session.getRecentlyShown();
        List<String> designsSeen = session.getDesignsSeen();

        long totalDesigns = cacheService.getActiveDesignCount();

        if (designsSeen.size() >= totalDesigns) {
            return null;
        }

        List<Design> candidates = fetchUnseenDesigns(championId, designsSeen);

        if (candidates.isEmpty()) {
            candidates = fetchDesignsExcluding(championId, recentlyShown);
        }

        if (candidates.isEmpty()) {
            candidates = fetchDesignsExcluding(championId, Collections.emptyList());
        }

        if (candidates.isEmpty()) {
            return null;
        }

        int minAppearances = candidates.stream()
                .mapToInt(Design::getAppearances)
                .min()
                .orElse(0);

        List<Design> leastShown = candidates.stream()
                .filter(d -> d.getAppearances() == minAppearances)
                .collect(Collectors.toList());

        Collections.shuffle(leastShown);
        return leastShown.get(0);
    }

    private List<Design> fetchUnseenDesigns(String championId, List<String> seenIds) {
        Set<String> exclude = new HashSet<>(seenIds);
        exclude.add(championId);
        Query query = Query.query(Criteria.where("active").is(true)
                .and("id").nin(exclude));
        query.fields().include("id", "title", "appearances", "imageUrl", "description");
        return mongoTemplate.find(query, Design.class);
    }

    private List<Design> fetchDesignsExcluding(String championId, List<String> excludeIds) {
        Set<String> exclude = new HashSet<>(excludeIds);
        exclude.add(championId);
        Query query = Query.query(Criteria.where("active").is(true)
                .and("id").nin(exclude));
        query.fields().include("id", "title", "appearances", "imageUrl", "description");
        return mongoTemplate.find(query, Design.class);
    }
}

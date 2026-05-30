package com.designduel.service;

import com.designduel.dto.DesignRanking;
import com.designduel.model.Design;
import com.designduel.model.Judge;
import com.designduel.model.TournamentResult;
import com.designduel.repository.DesignRepository;
import com.designduel.repository.JudgeRepository;
import com.designduel.repository.TournamentResultRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ResultsService {

    private final DesignRepository designRepository;
    private final JudgeRepository judgeRepository;
    private final TournamentResultRepository resultRepository;
    private final MongoTemplate mongoTemplate;

    public List<DesignRanking> getRankings() {
        List<Design> activeDesigns = designRepository.findByActiveTrue();

        Map<String, Integer> finalPickCounts = computeFinalPickCounts();

        return activeDesigns.stream()
                .map(d -> new DesignRanking(d, finalPickCounts.getOrDefault(d.getId(), 0)))
                .sorted(Comparator
                        .comparingInt(DesignRanking::getFinalPickCount).reversed()
                        .thenComparing(Comparator.comparingDouble((DesignRanking r) -> {
                            int comps = r.getDesign().getComparisons();
                            return comps > 0 ? (double) r.getDesign().getWins() / comps : 0.0;
                        }).reversed())
                        .thenComparing(r -> r.getDesign().getTitle()))
                .collect(Collectors.toList());
    }

    private Map<String, Integer> computeFinalPickCounts() {
        GroupOperation groupByFinalPick = Aggregation.group("finalPickId").count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(groupByFinalPick);
        AggregationResults<FinalPickCount> results = mongoTemplate.aggregate(
                aggregation, "judges", FinalPickCount.class);
        return results.getMappedResults().stream()
                .collect(Collectors.toMap(FinalPickCount::getId, FinalPickCount::getCount));
    }

    @Setter
    @Getter
    private static class FinalPickCount {
        private String id;
        private int count;
    }

    public DesignRanking getWinner() {
        List<DesignRanking> rankings = getRankings();
        return rankings.isEmpty() ? null : rankings.get(0);
    }

    public Design getMyFinalPick(String judgeId) {
        Judge judge = judgeRepository.findById(judgeId).orElse(null);
        if (judge == null || judge.getFinalPickId() == null) return null;
        return designRepository.findById(judge.getFinalPickId()).orElse(null);
    }

    public TournamentResult persistWinner() {
        DesignRanking winner = getWinner();
        if (winner == null) return null;

        List<DesignRanking> rankings = getRankings();
        int totalVotes = rankings.stream().mapToInt(r -> r.getDesign().getComparisons()).sum();

        TournamentResult result = TournamentResult.builder()
                .winnerId(winner.getDesign().getId())
                .winnerTitle(winner.getDesign().getTitle())
                .totalVotes(totalVotes)
                .build();

        log.info("Tournament winner persisted: {} ({}) with {} total votes",
                winner.getDesign().getTitle(), winner.getDesign().getId(), totalVotes);
        return resultRepository.save(result);
    }

    public List<TournamentResult> getHistory() {
        return resultRepository.findAllByOrderByGeneratedAtDesc();
    }
}

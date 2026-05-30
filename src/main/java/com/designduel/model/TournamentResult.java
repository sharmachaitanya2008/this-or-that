package com.designduel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "tournament_results")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TournamentResult {
    @Id
    private String id;
    private String winnerId;
    private String winnerTitle;
    private int totalVotes;

    @Builder.Default
    private Instant generatedAt = Instant.now();
}

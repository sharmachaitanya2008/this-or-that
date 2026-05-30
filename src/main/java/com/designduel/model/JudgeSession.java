package com.designduel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "judge_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JudgeSession {
    @Id
    private String id;

    @Version
    private Long version;

    @Indexed
    private String judgeId;
    private String currentChampionId;
    private String currentChallengerId;
    @Builder.Default private int votesCast = 0;
    @Builder.Default private List<String> designsSeen = new ArrayList<>();
    @Builder.Default private List<String> recentlyShown = new ArrayList<>();
    @Builder.Default private double progressPercentage = 0.0;
    @Builder.Default private boolean completed = false;

    @Builder.Default
    private Instant lastActive = Instant.now();
}

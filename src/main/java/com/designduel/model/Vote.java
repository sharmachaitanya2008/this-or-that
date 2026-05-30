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

@Document(collection = "votes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vote {
    @Id
    private String id;

    @Version
    private Long version;

    @Indexed
    private String judgeId;

    @Indexed
    private String winnerId;

    @Indexed
    private String loserId;

    @Indexed
    private String sessionId;

    @Builder.Default
    private Instant timestamp = Instant.now();
}

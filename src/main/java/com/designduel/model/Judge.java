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

@Document(collection = "judges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Judge {
    @Id
    private String id;

    @Version
    private Long version;

    @Indexed
    private String username;
    @Builder.Default private boolean active = true;
    @Builder.Default private boolean completed = false;
    @Builder.Default private int totalVotesCast = 0;

    @Indexed
    private String finalPickId;
    private String finalPickTitle;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant lastActive = Instant.now();
}

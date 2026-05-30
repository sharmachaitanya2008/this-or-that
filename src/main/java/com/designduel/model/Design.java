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

@Document(collection = "designs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Design {
    @Id
    private String id;
    private String title;
    private String description;
    private String imageUrl;

    @Version
    private Long version;

    @Indexed
    @Builder.Default private boolean active = true;
    @Builder.Default private int wins = 0;
    @Builder.Default private int losses = 0;
    @Builder.Default private int comparisons = 0;
    @Builder.Default private int appearances = 0;

    @Builder.Default
    private Instant createdAt = Instant.now();
}

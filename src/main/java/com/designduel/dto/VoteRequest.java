package com.designduel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VoteRequest {
    @NotBlank(message = "Winner ID is required")
    private String winnerId;

    @NotBlank(message = "Loser ID is required")
    private String loserId;
}

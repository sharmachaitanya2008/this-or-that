package com.designduel.dto;

import com.designduel.model.Design;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VoteResult {
    private Design winner;
    private Design challenger;
    private boolean completed;
    private int votesCast;
    private double progressPercentage;
}

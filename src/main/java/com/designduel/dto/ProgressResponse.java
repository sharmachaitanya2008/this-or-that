package com.designduel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ProgressResponse {
    private int votesCast;
    private double progressPercentage;
    private boolean completed;
}

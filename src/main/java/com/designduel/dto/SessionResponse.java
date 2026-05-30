package com.designduel.dto;

import com.designduel.model.Design;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class SessionResponse {
    private String sessionId;
    private String judgeId;
    private Design champion;
    private Design challenger;
    private int votesCast;
    private double progressPercentage;
    private boolean completed;
}

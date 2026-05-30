package com.designduel.dto;

import com.designduel.model.Design;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ResultsResponse {
    private List<DesignRanking> rankings;
    private DesignRanking winner;
    private Design myFinalPick;
}

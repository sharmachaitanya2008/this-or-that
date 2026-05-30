package com.designduel.dto;

import com.designduel.model.Design;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DesignRanking {
    private Design design;
    private int finalPickCount;
}

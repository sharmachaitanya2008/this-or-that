package com.designduel.dto;

import com.designduel.model.Design;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class DuelResponse {
    private Design champion;
    private Design challenger;
}

package com.designduel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class LoginResponse {
    private String judgeId;
    private String username;
    private String token;
    private long expiresIn;
}

package com.designduel.controller;

import com.designduel.model.Design;
import com.designduel.service.DesignService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/designs")
@AllArgsConstructor
public class DesignController {

    private final DesignService designService;

    @GetMapping("/duel")
    public ResponseEntity<Map<String, Design>> getDuel(@AuthenticationPrincipal String judgeId) {
        Map<String, Design> duel = designService.getDuel(judgeId);
        return ResponseEntity.ok(duel);
    }

    @GetMapping("/challenger")
    public ResponseEntity<Design> getChallenger(@AuthenticationPrincipal String judgeId) {
        Design challenger = designService.selectChallenger(judgeId);
        if (challenger == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(challenger);
    }

    @GetMapping
    public ResponseEntity<Page<Design>> getAllDesigns(@PageableDefault(size = 20, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(designService.getAllActive(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Design> getDesign(@PathVariable String id) {
        return ResponseEntity.ok(designService.getById(id));
    }
}

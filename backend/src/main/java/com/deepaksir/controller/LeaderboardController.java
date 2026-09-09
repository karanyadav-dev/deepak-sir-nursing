package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLeaderboard(
            @RequestParam(defaultValue = "weekly") String period,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<Map<String, Object>> leaderboard = List.of();
        return ResponseEntity.ok(ApiResponse.success("Leaderboard retrieved", leaderboard));
    }

    @GetMapping("/rank")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserRank(
            @RequestParam(defaultValue = "weekly") String period) {
        
        Map<String, Object> rank = Map.of(
            "rank", 0,
            "score", 0,
            "percentile", 0.0
        );
        return ResponseEntity.ok(ApiResponse.success("Rank retrieved", rank));
    }
}
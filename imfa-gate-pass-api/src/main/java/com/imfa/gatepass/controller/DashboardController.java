package com.imfa.gatepass.controller;

import com.imfa.gatepass.dto.*;
import com.imfa.gatepass.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/dashboard") @RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/stats")     public DashboardStatsDto stats()     { return service.getStats(); }
    @GetMapping("/chart")     public List<ChartDataDto> chart()     { return service.getChartData(); }
    @GetMapping("/analytics") public AnalyticsDto analytics()       { return service.getAnalytics(); }
}

package com.imfa.gatepass.controller;

import com.imfa.gatepass.dto.*;
import com.imfa.gatepass.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController @RequestMapping("/api/dashboard") @RequiredArgsConstructor
public class DashboardController {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private final DashboardService service;

    @GetMapping("/stats")  public DashboardStatsDto stats()  { return service.getStats(); }
    @GetMapping("/chart")  public List<ChartDataDto> chart() { return service.getChartData(); }

    @GetMapping("/analytics")
    public AnalyticsDto analytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate end   = endDate   != null ? LocalDate.parse(endDate)   : LocalDate.now(IST);
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : end.minusDays(29);
        return service.getAnalytics(start.toString(), end.toString());
    }
}

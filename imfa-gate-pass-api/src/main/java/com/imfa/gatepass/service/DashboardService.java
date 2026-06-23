package com.imfa.gatepass.service;

import com.imfa.gatepass.dto.*;
import com.imfa.gatepass.repository.GatePassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor
public class DashboardService {

    private final GatePassRepository repo;

    private static final Map<String, String> LOCATION_NAMES = Map.of(
        "therubali",    "Therubali Plant",
        "choudwar",     "Choudwar Plant",
        "kalinganagar", "Kalinganagar Plant",
        "bhubaneswar",  "Bhubaneswar HO"
    );
    private static final List<String> LOCATION_ORDER =
        List.of("therubali", "choudwar", "kalinganagar", "bhubaneswar");

    @Transactional(readOnly = true)
    public DashboardStatsDto getStats() {
        String today = LocalDate.now().toString();
        long total   = repo.findByVisitdate(today).size();
        long onsite  = repo.countByVisitdateAndStatus(today, "onsite");
        long pending = repo.countByVisitdateAndStatus(today, "pending");
        long cleared = repo.countByVisitdateAndStatus(today, "cleared");
        return DashboardStatsDto.builder()
            .totalToday(total).onsite(onsite).pending(pending).cleared(cleared).build();
    }

    @Transactional(readOnly = true)
    public List<ChartDataDto> getChartData() {
        String today = LocalDate.now().toString();
        var passes = repo.findByVisitdate(today);
        return LOCATION_ORDER.stream().map(locId -> ChartDataDto.builder()
            .locationId(locId)
            .locationName(LOCATION_NAMES.get(locId))
            .count(passes.stream().filter(p -> locId.equals(p.getLocation())).count())
            .build()
        ).toList();
    }
}

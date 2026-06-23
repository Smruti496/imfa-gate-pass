package com.imfa.gatepass.service;

import com.imfa.gatepass.dto.*;
import com.imfa.gatepass.repository.GatePassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class DashboardService {

    private final GatePassRepository repo;
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

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
        String today = LocalDate.now(IST).toString();
        long total   = repo.countByVisitDate(today);
        long onsite  = repo.countByVisitDateAndStatus(today, "onsite");
        long pending = repo.countByVisitDateAndStatus(today, "pending");
        long cleared = repo.countByVisitDateAndStatus(today, "cleared");
        return DashboardStatsDto.builder()
            .totalToday(total).onsite(onsite).pending(pending).cleared(cleared).build();
    }

    @Transactional(readOnly = true)
    public List<ChartDataDto> getChartData() {
        String today = LocalDate.now(IST).toString();
        List<Object[]> rows = repo.countByLocationForDate(today);
        Map<String, Long> countMap = rows.stream()
            .collect(Collectors.toMap(
                r -> (String) r[0], r -> (Long) r[1]));
        return LOCATION_ORDER.stream().map(locId -> ChartDataDto.builder()
            .locationId(locId).locationName(LOCATION_NAMES.get(locId))
            .count(countMap.getOrDefault(locId, 0L)).build()
        ).toList();
    }
}

package com.imfa.gatepass.service;

import com.imfa.gatepass.dto.*;
import com.imfa.gatepass.repository.GatePassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class DashboardService {

    private final GatePassRepository repo;
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private static final Map<String, String> LOCATION_NAMES = Map.of(
        "vijayanagar",         "Vijayanagar Works",
        "salem",               "Salem Works",
        "tarapur",             "Tarapur Works",
        "kalmeshwar",          "Kalmeshwar Works",
        "vasind",              "Vasind Works",
        "jsw-ispat",           "JSW Ispat Special Products",
        "bhushan-power-steel", "Bhushan Power & Steel",
        "dolvi",               "Dolvi Works"
    );
    private static final List<String> LOCATION_ORDER =
        List.of("vijayanagar", "salem", "tarapur", "kalmeshwar", "vasind", "jsw-ispat", "bhushan-power-steel", "dolvi");

    @Transactional(readOnly = true)
    public DashboardStatsDto getStats() {
        String today = LocalDate.now(IST).toString();
        long total   = repo.countByVisitDate(today);
        long onsite  = repo.countByVisitDateAndStatus(today, "onsite");
        long pending = repo.countByStatus("pending");   // all unvisited passes, any date
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

    @Transactional(readOnly = true)
    public AnalyticsDto getAnalytics(String startDate, String endDate) {
        return AnalyticsDto.builder()
            .statusDistribution(buildStatusDistribution(startDate, endDate))
            .locationStatusMatrix(buildLocationStatusMatrix(startDate, endDate))
            .genderDistribution(buildGenderDistribution(startDate, endDate))
            .hourlyTrend(buildTrend(repo.countGroupByHour(), 0))
            .monthlyTrend(buildTrend(repo.countGroupByMonth(), 0))
            .quarterlyTrend(buildTrend(repo.countGroupByQuarter(), 0))
            .yearlyTrend(buildTrend(repo.countGroupByYear(), 0))
            .build();
    }

    private List<StatusCountDto> buildStatusDistribution(String start, String end) {
        List<Object[]> rows = repo.countGroupByStatusBetween(start, end);
        long total = rows.stream().mapToLong(r -> toLong(r[1])).sum();
        return rows.stream().map(r -> StatusCountDto.builder()
            .status((String) r[0])
            .count(toLong(r[1]))
            .percentage(total == 0 ? 0 : Math.round(toLong(r[1]) * 1000.0 / total) / 10.0)
            .build()
        ).toList();
    }

    private List<LocationStatusDto> buildLocationStatusMatrix(String start, String end) {
        List<Object[]> rows = repo.countGroupByLocationAndStatusBetween(start, end);
        Map<String, Map<String, Long>> matrix = new HashMap<>();
        for (Object[] r : rows) {
            String loc = (String) r[0];
            String st  = (String) r[1];
            matrix.computeIfAbsent(loc, k -> new HashMap<>()).put(st, toLong(r[2]));
        }
        return LOCATION_ORDER.stream().map(locId -> {
            Map<String, Long> m = matrix.getOrDefault(locId, Map.of());
            long p = m.getOrDefault("pending", 0L);
            long o = m.getOrDefault("onsite",  0L);
            long c = m.getOrDefault("cleared", 0L);
            return LocationStatusDto.builder()
                .locationId(locId).locationName(LOCATION_NAMES.get(locId))
                .pending(p).onsite(o).cleared(c).total(p + o + c)
                .build();
        }).toList();
    }

    private List<GenderCountDto> buildGenderDistribution(String start, String end) {
        List<Object[]> rows = repo.countGroupByGenderBetween(start, end);
        long total = rows.stream().mapToLong(r -> toLong(r[1])).sum();
        return rows.stream().map(r -> GenderCountDto.builder()
            .gender((String) r[0])
            .count(toLong(r[1]))
            .percentage(total == 0 ? 0 : Math.round(toLong(r[1]) * 1000.0 / total) / 10.0)
            .build()
        ).toList();
    }

    private List<TrendPointDto> buildTrend(List<Object[]> rows, int labelIdx) {
        return rows.stream().map(r -> TrendPointDto.builder()
            .label((String) r[labelIdx])
            .count(toLong(r[rows.get(0).length - 1]))
            .build()
        ).toList();
    }

    private static long toLong(Object v) {
        if (v instanceof Long l) return l;
        if (v instanceof Number n) return n.longValue();
        if (v instanceof BigDecimal bd) return bd.longValue();
        return 0L;
    }
}

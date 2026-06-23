package com.imfa.gatepass.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class DashboardStatsDto {
    long totalToday;
    long onsite;
    long pending;
    long cleared;
}

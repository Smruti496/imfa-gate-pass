package com.imfa.gatepass.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class AnalyticsDto {
    List<StatusCountDto> statusDistribution;
    List<LocationStatusDto> locationStatusMatrix;
    List<GenderCountDto> genderDistribution;
    List<TrendPointDto> hourlyTrend;
    List<TrendPointDto> monthlyTrend;
    List<TrendPointDto> quarterlyTrend;
    List<TrendPointDto> yearlyTrend;
}

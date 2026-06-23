package com.imfa.gatepass.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ChartDataDto {
    String locationId;
    String locationName;
    long count;
}

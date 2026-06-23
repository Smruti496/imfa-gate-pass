package com.imfa.gatepass.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TrendPointDto {
    String label;
    long count;
}

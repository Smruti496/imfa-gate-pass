package com.imfa.gatepass.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class StatusCountDto {
    String status;
    long count;
    double percentage;
}

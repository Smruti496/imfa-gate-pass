package com.imfa.gatepass.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class GenderCountDto {
    String gender;
    long count;
    double percentage;
}

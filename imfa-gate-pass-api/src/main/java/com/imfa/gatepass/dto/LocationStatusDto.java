package com.imfa.gatepass.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class LocationStatusDto {
    String locationId;
    String locationName;
    long pending;
    long onsite;
    long cleared;
    long total;
}

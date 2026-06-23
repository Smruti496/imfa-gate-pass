package com.imfa.gatepass.dto;

import com.imfa.gatepass.model.GatePass;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data @Builder
public class GatePassResponse {
    UUID id;
    String passNo;
    String visitorName;
    String companyName;
    String whomToVisit;
    String photoId;
    String photoIdType;
    String location;
    String gate;
    String visitDate;
    String visitTime;
    String purpose;
    String photo;
    String status;
    String checkInTime;
    String checkOutTime;
    String createdTime;

    public static GatePassResponse from(GatePass g) {
        return GatePassResponse.builder()
            .id(g.getId()).passNo(g.getPassNo())
            .visitorName(g.getVisitorName()).companyName(g.getCompanyName())
            .whomToVisit(g.getWhomToVisit()).photoId(g.getPhotoId()).photoIdType(g.getPhotoIdType())
            .location(g.getLocation()).gate(g.getGate())
            .visitDate(g.getVisitDate()).visitTime(g.getVisitTime())
            .purpose(g.getPurpose()).photo(g.getPhoto())
            .status(g.getStatus()).checkInTime(g.getCheckInTime()).checkOutTime(g.getCheckOutTime())
            .createdTime(g.getCreatedTime() != null ? g.getCreatedTime().toString() : null)
            .build();
    }
}

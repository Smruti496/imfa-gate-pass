package com.imfa.gatepass.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GatePassRequest {
    @NotBlank String visitorName;
    @NotBlank String companyName;
    @NotBlank String whomToVisit;
    @NotBlank String photoId;         // UID number
    @NotBlank String photoIdType;     // "Aadhaar" / "PAN" / "Voter ID"
    @NotBlank String gender;          // "Male" | "Female" | "Other"
    @NotBlank String location;
    @NotBlank String gate;
    @NotBlank String visitDate;       // "YYYY-MM-DD"
    @NotBlank String visitTime;       // "HH:MM"
    @NotBlank String purpose;
    String photo;                     // base64, optional
    @NotBlank String waNumber;        // WhatsApp number, required
}

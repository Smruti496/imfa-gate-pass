package com.imfa.gatepass.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gate_pass", schema = "public")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GatePass {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String visitorName;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "whometovisit")
    private String whomToVisit;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "status")
    private String status;

    @Column(name = "visitdate")
    private String visitDate;    // stored as text "YYYY-MM-DD"

    @Column(name = "visittime")
    private String visitTime;    // stored as text "HH:MM"

    @Column(name = "createdtime")
    private LocalDateTime createdTime;

    @Column(name = "lastupdatedtime")
    private LocalDateTime lastUpdatedTime;

    @Column(name = "photo", columnDefinition = "text")
    private String photo;

    @Column(name = "photoid")
    private String photoId;         // UID number (Aadhaar/PAN/Voter ID)

    @Column(name = "photoid_type")
    private String photoIdType;     // "Aadhaar" / "PAN" / "Voter ID"

    // Added columns (ALTER TABLE above)
    @Column(name = "pass_no")
    private String passNo;

    @Column(name = "location")
    private String location;

    @Column(name = "gate")
    private String gate;

    @Column(name = "check_in_time")
    private String checkInTime;     // stored as text "HH:MM"

    @Column(name = "check_out_time")
    private String checkOutTime;    // stored as text "HH:MM"

    @Column(name = "gender")
    private String gender;          // "Male" | "Female" | "Other"

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID();
        if (status == null) status = "pending";
        createdTime = LocalDateTime.now();
        lastUpdatedTime = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() { lastUpdatedTime = LocalDateTime.now(); }
}

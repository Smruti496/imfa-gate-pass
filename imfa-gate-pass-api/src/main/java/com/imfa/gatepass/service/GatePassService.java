package com.imfa.gatepass.service;

import com.imfa.gatepass.dto.*;
import com.imfa.gatepass.model.GatePass;
import com.imfa.gatepass.repository.GatePassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GatePassService {

    private final GatePassRepository repo;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private String generatePassNo() {
        int year = LocalDate.now().getYear();
        long count = repo.countByYearPrefix(String.valueOf(year));
        return String.format("GP-%d-%04d", year, count + 1);
    }

    private String today() { return LocalDate.now().toString(); }
    private String nowHHMM() { return LocalTime.now().format(TIME_FMT); }

    @Transactional
    public GatePassResponse create(GatePassRequest req) {
        GatePass pass = GatePass.builder()
            .id(UUID.randomUUID())
            .passNo(generatePassNo())
            .visitorName(req.getVisitorName()).companyName(req.getCompanyName())
            .whomToVisit(req.getWhomToVisit()).photoId(req.getPhotoId()).photoIdType(req.getPhotoIdType())
            .location(req.getLocation()).gate(req.getGate())
            .visitDate(req.getVisitDate()).visitTime(req.getVisitTime())
            .purpose(req.getPurpose()).photo(req.getPhoto())
            .status("pending")
            .build();
        return GatePassResponse.from(repo.save(pass));
    }

    @Transactional(readOnly = true)
    public Page<GatePassResponse> list(String location, String status, String query,
                                       boolean showAllDates, int page, int size) {
        String date = today();
        String qlike = query == null || query.isBlank() ? "" : "%" + query.toLowerCase(Locale.ROOT) + "%";
        String q = query == null ? "" : query;
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAllByFilters(date, location, status, q, qlike, showAllDates, pageable)
                   .map(GatePassResponse::from);
    }

    @Transactional(readOnly = true)
    public GatePassResponse getById(UUID id) {
        return repo.findById(id).map(GatePassResponse::from)
                   .orElseThrow(() -> new RuntimeException("Gate pass not found: " + id));
    }

    @Transactional
    public GatePassResponse checkIn(UUID id) {
        GatePass pass = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Gate pass not found: " + id));
        if (!"pending".equals(pass.getStatus()))
            throw new IllegalStateException("Only pending passes can be checked in");
        pass.setStatus("onsite");
        pass.setCheckInTime(nowHHMM());
        return GatePassResponse.from(repo.save(pass));
    }

    @Transactional
    public GatePassResponse checkOut(UUID id) {
        GatePass pass = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Gate pass not found: " + id));
        if (!"onsite".equals(pass.getStatus()))
            throw new IllegalStateException("Only on-site visitors can be checked out");
        pass.setStatus("cleared");
        pass.setCheckOutTime(nowHHMM());
        return GatePassResponse.from(repo.save(pass));
    }

    @Transactional
    public void cancel(UUID id) {
        GatePass pass = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Gate pass not found: " + id));
        if (!"pending".equals(pass.getStatus()))
            throw new IllegalStateException("Only pending passes can be cancelled");
        repo.deleteById(id);
    }
}

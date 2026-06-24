package com.imfa.gatepass.service;

import com.imfa.gatepass.dto.*;
import com.imfa.gatepass.model.GatePass;
import com.imfa.gatepass.repository.GatePassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GatePassService {

    private final GatePassRepository repo;
    private final GatePassPdfService pdfService;

    @Autowired(required = false)
    private CheckinWebhookService checkinWebhookService;

    @Autowired(required = false)
    private CheckinEmailService checkinEmailService;

    @Value("${app.base-url}")
    private String baseUrl;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private String generatePassNo() {
        int year = LocalDate.now(IST).getYear();
        long fromSeq = repo.nextPassSeq();
        long fromMax = repo.maxPassNoForYear(year) + 1;
        long seq = Math.max(fromSeq, fromMax);
        return String.format("GP-%d-%04d", year, seq);
    }

    private String today() { return LocalDate.now(IST).toString(); }
    private String nowHHMM() { return LocalTime.now(IST).format(TIME_FMT); }

    @Transactional
    public GatePassResponse create(GatePassRequest req) {
        GatePass pass = GatePass.builder()
            .id(UUID.randomUUID())
            .passNo(generatePassNo())
            .visitorName(req.getVisitorName()).companyName(req.getCompanyName())
            .whomToVisit(req.getWhomToVisit()).photoId(req.getPhotoId()).photoIdType(req.getPhotoIdType())
            .gender(req.getGender())
            .waNumber(req.getWaNumber())
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
        return repo.findAllByFilters(date, location, status, q, qlike, String.valueOf(showAllDates), pageable)
                   .map(GatePassResponse::from);
    }

    @Transactional(readOnly = true)
    public GatePassResponse getById(UUID id) {
        return repo.findById(id).map(GatePassResponse::from)
                   .orElseThrow(() -> new RuntimeException("Gate pass not found: " + id));
    }

    @Transactional
    public GatePassResponse checkIn(UUID id, List<String> emails) {
        GatePass pass = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Gate pass not found: " + id));
        if (!"pending".equalsIgnoreCase(pass.getStatus()))
            throw new IllegalStateException("Only pending passes can be checked in");
        pass.setStatus("onsite");
        pass.setCheckInTime(nowHHMM());
        GatePassResponse response = GatePassResponse.from(repo.save(pass));
        final String passNo = pass.getPassNo();
        final String waNum  = pass.getWaNumber();
        final String pdfUrl = baseUrl + "/api/gate-passes/" + pass.getId() + "/pdf";
        final java.util.UUID passId = pass.getId();
        final List<String> emailsCopy = List.copyOf(emails);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (checkinWebhookService != null)
                    checkinWebhookService.notify(passNo, waNum, pdfUrl);
                if (checkinEmailService != null && !emailsCopy.isEmpty())
                    checkinEmailService.notify(passId, passNo, emailsCopy, pdfUrl);
            }
        });
        return response;
    }

    @Transactional
    public GatePassResponse checkOut(UUID id) {
        GatePass pass = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Gate pass not found: " + id));
        if (!"onsite".equalsIgnoreCase(pass.getStatus()))
            throw new IllegalStateException("Only on-site visitors can be checked out");
        pass.setStatus("cleared");
        pass.setCheckOutTime(nowHHMM());
        return GatePassResponse.from(repo.save(pass));
    }

    @Transactional
    public void cancel(UUID id) {
        GatePass pass = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Gate pass not found: " + id));
        if (!"pending".equalsIgnoreCase(pass.getStatus()))
            throw new IllegalStateException("Only pending passes can be cancelled");
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public byte[] getPdf(UUID id) {
        GatePass pass = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Gate pass not found: " + id));
        return pdfService.generate(pass);
    }
}

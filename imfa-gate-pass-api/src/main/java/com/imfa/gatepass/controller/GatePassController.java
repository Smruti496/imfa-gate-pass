package com.imfa.gatepass.controller;

import com.imfa.gatepass.dto.*;
import com.imfa.gatepass.service.GatePassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/gate-passes") @RequiredArgsConstructor
public class GatePassController {

    private final GatePassService service;

    @GetMapping
    public Page<GatePassResponse> list(
        @RequestParam(defaultValue = "all") String location,
        @RequestParam(defaultValue = "all") String status,
        @RequestParam(defaultValue = "")    String q,
        @RequestParam(defaultValue = "false") boolean showAllDates,
        @RequestParam(defaultValue = "0")   int page,
        @RequestParam(defaultValue = "50")  int size
    ) {
        return service.list(location, status, q, showAllDates, page, size);
    }

    @GetMapping("/{id}")
    public GatePassResponse getById(@PathVariable UUID id) { return service.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GatePassResponse create(@Valid @RequestBody GatePassRequest req) {
        return service.create(req);
    }

    @PatchMapping("/{id}/checkin")
    public GatePassResponse checkIn(@PathVariable UUID id) { return service.checkIn(id); }

    @PatchMapping("/{id}/checkout")
    public GatePassResponse checkOut(@PathVariable UUID id) { return service.checkOut(id); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id) { service.cancel(id); }
}

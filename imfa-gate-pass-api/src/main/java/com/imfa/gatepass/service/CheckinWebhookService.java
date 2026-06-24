package com.imfa.gatepass.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.checkin.webhook.enabled", havingValue = "true")
public class CheckinWebhookService {

    private final RestClient restClient;
    private final String webhookUrl;

    public CheckinWebhookService(RestClient checkinRestClient,
                                 @Value("${app.checkin.webhook.url}") String webhookUrl) {
        this.restClient = checkinRestClient;
        this.webhookUrl = webhookUrl;
    }

    public void notify(String passNo, String waNumber, String pdfUrl) {
        if (waNumber == null || waNumber.isBlank()) {
            log.info("Checkin webhook skipped — no WhatsApp number for pass {}", passNo);
            return;
        }
        String digits = waNumber.replaceAll("[^\\d]", "");
        String receiver = digits.length() > 10 ? digits.substring(digits.length() - 10) : digits;
        log.info("Checkin webhook number — raw: {}, digits: {}, receiver: {}", waNumber, digits, receiver);
        if (receiver.length() != 10) {
            log.warn("Checkin webhook skipped — WhatsApp number not 10 digits for pass {}: {}", passNo, waNumber);
            return;
        }
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("gatepass_no", passNo);
            payload.put("status", "Check In Completed");
            payload.put("receiver", receiver);
            if (pdfUrl != null && !pdfUrl.isBlank())
                payload.put("gatepass", pdfUrl);

            log.info("Checkin webhook payload for pass {}: {}", passNo, payload);

            restClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

            log.info("Checkin webhook sent for pass {}", passNo);
        } catch (Exception ex) {
            log.warn("Checkin webhook failed for pass {}: {}", passNo, ex.getMessage());
        }
    }
}

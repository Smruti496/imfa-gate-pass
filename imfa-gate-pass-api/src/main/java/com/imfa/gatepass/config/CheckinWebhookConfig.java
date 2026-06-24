package com.imfa.gatepass.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnProperty(name = "app.checkin.webhook.enabled", havingValue = "true")
public class CheckinWebhookConfig {

    @Bean
    public RestClient checkinRestClient() {
        return RestClient.create();
    }
}

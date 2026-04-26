package com.akash.medicalproj.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    @Value("${openai.api.key:}")
    private String openAiApiKey;


    @PostConstruct
    public void initStripe() {
        if (stripeSecretKey != null && !stripeSecretKey.isBlank()) {
            Stripe.apiKey = stripeSecretKey;
        }
    }

    @Bean
    public String stripePublicKey(@Value("${stripe.public.key:}") String publicKey) {
        return publicKey;
    }
}

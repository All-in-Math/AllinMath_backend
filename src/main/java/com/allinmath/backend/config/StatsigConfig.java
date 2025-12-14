package com.allinmath.backend.config;

import com.allinmath.backend.util.Logger;
import com.statsig.sdk.InitializationDetails;
import com.statsig.sdk.Statsig;
import com.statsig.sdk.StatsigOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
public class StatsigConfig {

    @Value("${statsig.secret}")
    private String statsigSecret;

    @PostConstruct
    public void initialize() {
        if (statsigSecret == null || statsigSecret.isEmpty()) {
            Logger.w("Statsig secret not found. Skipping initialization.");
            return;
        }

        StatsigOptions options = new StatsigOptions();
        // Configure options if needed, e.g.:
        // options.setTier(Tier.DEVELOPMENT);

        try {
            CompletableFuture<InitializationDetails> initFuture = Statsig.initializeAsync(statsigSecret, options);
            initFuture.get(); // Wait for initialization
            Logger.i("Statsig initialized successfully.");
        } catch (InterruptedException | ExecutionException e) {
            Logger.e(e, "Failed to initialize Statsig: %s", e.getMessage());
            // Restore interrupted state
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        Statsig.shutdown();
        Logger.i("Statsig shutdown.");
    }
}

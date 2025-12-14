package com.allinmath.backend.config;

import com.allinmath.backend.util.Logger;
import com.statsig.InitializeDetails;
import com.statsig.Statsig;
import com.statsig.StatsigOptions;
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

    private Statsig statsig;

    @PostConstruct
    public void initialize() {
        if (statsigSecret == null || statsigSecret.isEmpty()) {
            Logger.w("Statsig secret not found. Skipping initialization.");
            return;
        }

        StatsigOptions options = new StatsigOptions.Builder().build();
        // Configure options if needed, e.g.:
        // options.setTier(Tier.DEVELOPMENT);

        try {
            statsig = new Statsig(statsigSecret, options);
            CompletableFuture<InitializeDetails> initFuture = statsig.initializeWithDetails();
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
        if (statsig != null) {
            statsig.shutdown();
        }
        Logger.i("Statsig shutdown.");
    }
}

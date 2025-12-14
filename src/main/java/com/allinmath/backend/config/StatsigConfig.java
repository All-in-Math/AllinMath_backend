package com.allinmath.backend.config;

import com.allinmath.backend.util.Logger;
import com.statsig.sdk.StatsigServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatsigConfig {

    @Value("${statsig.server.secret}")
    private String serverSecret;

    private StatsigServer statsigServer;

    @PostConstruct
    public void initialize() {
        try {
            Logger.i("Initializing Statsig with server secret...");
            statsigServer = StatsigServer.create();
            statsigServer.initializeAsync(serverSecret, null).join();
            Logger.i("Statsig initialized successfully");
        } catch (Exception e) {
            Logger.e("Failed to initialize Statsig: %s", e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            Logger.i("Shutting down Statsig...");
            if (statsigServer != null) {
                statsigServer.shutdown();
            }
            Logger.i("Statsig shutdown successfully");
        } catch (Exception e) {
            Logger.e("Error during Statsig shutdown: %s", e.getMessage());
        }
    }

    public StatsigServer getStatsigServer() {
        return statsigServer;
    }
}

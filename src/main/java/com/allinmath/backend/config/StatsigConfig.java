package com.allinmath.backend.config;

import com.allinmath.backend.util.Logger;
import com.statsig.sdk.StatsigServer;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatsigConfig {

    @Value("${statsig.server.secret}")
    private String serverSecret;

    private StatsigServer statsigServer;

    @Bean
    public StatsigServer statsigServer() {
        try {
            Logger.i("Initializing Statsig with server secret...");
            statsigServer = StatsigServer.create();
            statsigServer.initializeAsync(serverSecret, null).join();
            Logger.i("Statsig initialized successfully");
            return statsigServer;
        } catch (Exception e) {
            Logger.e("Failed to initialize Statsig: %s", e.getMessage());
            throw new RuntimeException("Failed to initialize Statsig", e);
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
}

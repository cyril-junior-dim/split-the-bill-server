package com.splitthebill.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConditionalOnProperty(
        value = "splitthebill.app.scheduling-enabled", havingValue = "true"
)
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

package com.digitaladvertisingmanagement.config;

import com.google.ads.googleads.lib.GoogleAdsClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleAdsConfig {
  @Bean
  @ConditionalOnProperty(name = "google.ads.enabled", havingValue = "true")
  public GoogleAdsClient googleAdsClient() {
    return GoogleAdsClient.newBuilder().fromEnvironment().build();
  }
}

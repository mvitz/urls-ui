package com.innoq.urls.ui;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Reporter;

@SpringBootApplication
public class UrlsUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlsUiApplication.class, args);
    }

    @Bean
    public RestTemplate httpClient() {
        return new RestTemplate();
    }

    @Bean
    public MetricRegistry metricsRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public Reporter reporter(MetricRegistry metrics) {
        final ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS);
        return reporter;
    }

}

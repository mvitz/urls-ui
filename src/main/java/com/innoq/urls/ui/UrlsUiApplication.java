package com.innoq.urls.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class UrlsUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlsUiApplication.class, args);
    }

    @Bean
    public RestTemplate httpClient() {
        return new RestTemplate();
    }

}

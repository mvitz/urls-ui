package com.innoq.urls.ui.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.innoq.urls.ui.web.RequestIdFilter;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

@Service
public final class HashService {

    private static final Logger LOGGER = LogManager.getLogger(HashService.class);

    private final RestTemplate rest;

    @Autowired
    public HashService(RestTemplate rest) {
        this.rest = rest;
    }

    public String hash(String url) {
        final String hash = new HashCommand(rest, url).execute();
        LOGGER.info("'{}' hashed as '{}'", url, hash);
        return hash;
    }

    public Optional<String> resolve(String hash) {
        final Optional<String> value = new ResolveCommand(rest, hash).execute();
        LOGGER.info("'{}' resolved to '{}'", hash, value.orElse(null));
        return value;
    }

    private static final class HashCommand extends HystrixCommand<String> {

        private final RestTemplate client;
        private final String url;

        public HashCommand(RestTemplate client, String url) {
            super(HystrixCommandGroupKey.Factory.asKey("HashCommand"));
            this.client = client;
            this.url = url;
        }

        @Override
        protected String run() throws Exception {
            final HttpHeaders headers = new HttpHeaders();
            headers.add(RequestIdFilter.REQUEST_ID_HEADER, MDC.get(RequestIdFilter.REQUEST_ID));
            final HttpEntity<String> request = new HttpEntity<>(url, headers);
            final ResponseEntity<String> response = client.postForEntity("http://localhost:8081", request, String.class);
            return response.getBody();
        }

    }

    private static final class ResolveCommand extends HystrixCommand<Optional<String>> {

        private final RestTemplate client;
        private final String hash;

        public ResolveCommand(RestTemplate client, String hash) {
            super(HystrixCommandGroupKey.Factory.asKey("ResolveCommand"));
            this.client = client;
            this.hash = hash;
        }

        @Override
        protected Optional<String> run() throws Exception {
            final Map<String, String> pathVariables = new HashMap<>();
            pathVariables.put("hash", hash);
            final ResponseEntity<String> response = client.getForEntity("http://localhost:8081/{hash}", String.class, pathVariables);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.of(response.getBody());
            } else {
                return Optional.empty();
            }
        }

    }

}

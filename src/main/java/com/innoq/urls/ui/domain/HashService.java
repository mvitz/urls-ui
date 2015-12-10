package com.innoq.urls.ui.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

@Service
public final class HashService {

    private final RestTemplate rest;

    @Autowired
    public HashService(RestTemplate rest) {
        this.rest = rest;
    }

    public String hash(String url) {
        return new HashCommand(rest, url).execute();
    }

    public Optional<String> resolve(String hash) {
        return new ResolveCommand(rest, hash).execute();
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
            final HttpEntity<String> request = new HttpEntity<String>(url);
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

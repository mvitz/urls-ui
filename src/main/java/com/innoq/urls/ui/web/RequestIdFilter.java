package com.innoq.urls.ui.web;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

@Component
public final class RequestIdFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "x-request-id";
    private static final String REQUEST_ID = "requestId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        String requestId = ((HttpServletRequest) request).getHeader(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        ThreadContext.put(REQUEST_ID, requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            ThreadContext.remove(REQUEST_ID);
        }
    }

    @Override
    public void destroy() {
    }

}

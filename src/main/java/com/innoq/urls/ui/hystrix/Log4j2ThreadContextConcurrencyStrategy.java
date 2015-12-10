package com.innoq.urls.ui.hystrix;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.ThreadContext;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

public final class Log4j2ThreadContextConcurrencyStrategy
        extends HystrixConcurrencyStrategy {

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new Log4j2ThreadContextCallableWrapper<>(callable);
    }

    private static final class Log4j2ThreadContextCallableWrapper<K>
            implements Callable<K> {

        private final Callable<K> actual;
        private final Map<String, String> context;

        public Log4j2ThreadContextCallableWrapper(Callable<K> actual) {
            this.actual = actual;
            this.context = ThreadContext.getImmutableContext();
        }

        @Override
        public K call() throws Exception {
            final Map<String, String> existingContext = ThreadContext.getImmutableContext();
            try {
                ThreadContext.clearMap();
                context.forEach(ThreadContext::put);
                return actual.call();
            } finally {
                ThreadContext.clearMap();
                existingContext.forEach(ThreadContext::put);
            }
        }

    }

}

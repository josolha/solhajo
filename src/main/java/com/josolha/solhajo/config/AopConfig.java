package com.josolha.solhajo.config;


import com.josolha.solhajo.aop.logtrace.LogTrace;
import com.josolha.solhajo.aop.logtrace.LogTraceAspect;
import com.josolha.solhajo.aop.logtrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }

    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }
}

package com.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "provider")
public class AuditingConfig {
    @Bean
    public AuditorAware<String> provider(){
        return new AuditorAwareImpl();
    }


//    @Bean
//    public AuditorAware<String> auditorAware(){
//        return new AuditorAware<String>() {
//            @Override
//            public Optional<String> getCurrentAuditor() {
//                return Optional.of(SecurityContextHolder.getContext().getAuthentication()!=null?
//                        SecurityContextHolder.getContext().getAuthentication().getName():"system");
//            }
//        };
//    }
}

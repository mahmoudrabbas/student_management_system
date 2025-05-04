package com.system.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
public class GlobalConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

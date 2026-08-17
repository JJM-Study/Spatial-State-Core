package com.dev.ssc.infrastructure.config;

import com.dev.ssc.application.port.in.SpatialEngineUseCase;
import com.dev.ssc.application.port.in.dto.SpatialSearchQuery;
import com.dev.ssc.application.port.out.SpatialEnginePort;
import com.dev.ssc.core.service.SpatialEngineService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.util.List;

@Configuration
public class ServiceConfig {

    @Bean
    public SpatialEngineUseCase spatialEngineService(List<SpatialEnginePort> ports){
        return new SpatialEngineService(ports);
    }

}

package com.dev.ssc.application.port.out;



// NearbyResponse가 Infrasturcture에 종속되어 있는 문제성. ->
// 개별 DTO 만드는 게 나을 듯.
//import com.dev.ssc.infrastructure.out.fastapi.dto.NearbyResponse;
import com.dev.ssc.core.dto.SpatialResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


public interface SpatialEnginePort {
    public Mono<SpatialResult> callExternalEngine(Double lat, Double lon, Integer k);
}

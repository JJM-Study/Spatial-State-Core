package com.dev.ssc.infrastructure.in.web;

import com.dev.ssc.application.port.out.SpatialEnginePort;
import com.dev.ssc.core.dto.SpatialResult;
import com.dev.ssc.infrastructure.out.fastapi.dto.NearbyResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
// /api/v1/spatial 이냐, /api/spatial/v1 이냐 ..
@RequestMapping("/api/v1/spatial")
public class NearbyController {
    private final SpatialEnginePort spatialEnginePort;

    NearbyController(SpatialEnginePort spatialEnginePort) {
        this.spatialEnginePort = spatialEnginePort;
    }


    // 굳이 NearbyResponse를, 개별 infrastructure에 dto로 정의할 필요가 있을까?
    // 어차피 core의 dto에 종속될텐데..
    // -> 그렇다면 JsonProperty 의 문제는?
   @PostMapping("/nearby")
   public Mono<SpatialResult> findNearby(Double lat, Double lon, Integer k) {

       return spatialEnginePort.callExternalEngine(lat, lon, k);
   }
}

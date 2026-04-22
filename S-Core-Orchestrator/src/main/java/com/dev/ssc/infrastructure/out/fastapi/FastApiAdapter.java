package com.dev.ssc.infrastructure.out.fastapi;

import com.dev.ssc.application.port.out.SpatialEnginePort;
import com.dev.ssc.infrastructure.out.fastapi.dto.NearbyResponse;
import com.dev.ssc.infrastructure.out.fastapi.dto.SearchRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// 생성자를 하나하나 쓰는 게 나을까, Bean에 올려두는 게 나을까 생각..
@Component
public class FastApiAdapter implements SpatialEnginePort {

    private final WebClient webClient = WebClient.create("http://127.0.0.1:8000");

    // 안쪽(Application/Core)은 바깥쪽(Infrastructure)을 절대로 몰라야 한다.

    @Override
    public Mono<NearbyResponse> callExternalEngine(Double lat, Double lon, Integer k) {

        return webClient
                .post()
                .uri("/nearby")
                .retrieve()
                .bodyToMono(NearbyResponse.class)
                .bodyValue(new SearchRequest(lat, lon, k));
    }
}

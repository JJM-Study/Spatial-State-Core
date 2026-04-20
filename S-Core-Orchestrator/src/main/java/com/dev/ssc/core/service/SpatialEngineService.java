//package com.dev.ssc.core.service;
//
//import com.dev.ssc.infrastructure.out.fastapi.dto.NearbyResponse;
//import com.dev.ssc.infrastructure.out.fastapi.dto.SearchRequest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Service
//public class SpatialEngineService {
//    private final WebClient webClient = WebClient.create("http://127.0.0.1:8000");
//
//
//    // 의존성을 줄이고, 엔진이 파이썬에서 다른 것으로 바뀔 수도 있다는 것에 대비한 추상화
//    public Mono<NearbyResponse> findNearby(Double lan, Double lon, Integer k) {
//
////        SearchRequest request = new SearchRequest(lan, lon, k);
////
////        return webClient
////                .post()
////                .uri("/nearby")
////                .bodyValue(request)
////                .retrieve()
////                .bodyToMono(NearbyResponse.class);
//
//        return
//    }
//
//
//    public Mono<NearbyResponse> callExternalEngine () {
//
//        return
//                request.myLon()
//                request.myLat().
//
//    }
//
//}

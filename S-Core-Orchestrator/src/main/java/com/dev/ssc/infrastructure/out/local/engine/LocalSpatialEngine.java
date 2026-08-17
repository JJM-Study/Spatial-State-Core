package com.dev.ssc.infrastructure.out.local.engine;


import com.dev.ssc.application.port.in.dto.SpatialSearchQuery;
import com.dev.ssc.application.port.out.dto.SpatialEngineRequest;
import com.dev.ssc.core.dto.SpatialResult;
import com.dev.ssc.core.service.SpatialEngineService;
import com.dev.ssc.infrastructure.out.fastapi.FastApiAdapter;
import com.dev.ssc.infrastructure.out.fastapi.dto.NearbyResponse;
import com.dev.ssc.infrastructure.out.fastapi.dto.SearchRequest;
import com.dev.ssc.infrastructure.out.local.LocalEngineAdapter;
import com.dev.ssc.infrastructure.out.local.engine.dto.LocalEngineRequest;
import com.dev.ssc.infrastructure.out.local.engine.dto.LocalEngineResponse;
import com.github.davidmoten.rtree2.Entry;
import com.github.davidmoten.rtree2.RTree;
import com.github.davidmoten.rtree2.geometry.Geometries;
import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.geometry.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
//import java.util.concurrent.atomic.AtomicReference;

@Component
public class LocalSpatialEngine {

//    private final AtomicReference<> atomicReference = new AtomicReference(); // 현재 자체는 동시성이 일어날 위험성은 없다고 봐도.

    private static final Logger logger = LogManager.getLogger(LocalSpatialEngine.class);

    public record NodeData(int nodeId, double lat, double lon) {}

    final RTree<Integer, Point> localRtree = RTree.star().create();

    private final Map<Integer, NodeData> nodeStorage = new ConcurrentHashMap<>();

    public LocalSpatialEngine() {
        //  # 서울역 기준 반경 약 10km 이내 랜덤 좌표
        double centerLat = 37.5559;
        double centerLon = 126.9723;
        final double radiusInMeters = 10000; // 10km 내

        final double meterPerLatDegree = 111000.0;
        final double meterPerLotDegree = 111000.0 * Math.cos(Math.toRadians(centerLat));

        // 10km를 위도/경도 단위의 '최대 반지름'으로 변환
        double maxDeltaLat = radiusInMeters / meterPerLatDegree;
        double maxDeltaLon = radiusInMeters / meterPerLotDegree;

        for (int i = 0; i < 1000; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double r = Math.sqrt(Math.random());

            double randomLat = centerLat + (r * maxDeltaLat * Math.sin(angle));
            double randomLon = centerLon + (r * maxDeltaLon * Math.cos(angle));

            nodeStorage.put(i, new NodeData(i, randomLat, randomLon));


            localRtree.add(i, Geometries.point(randomLat, randomLon));
        }
        logger.info("Rtree 임의 10km 내 장소 1000군데 할당 완료.");
    }

    public Mono<LocalEngineResponse> get_nearby(LocalEngineRequest request) {
        logger.info("Local Get_Nearby executed");

        int k = 3;

        Point myPoint = Geometries.point(request.lat(), request.lon());

        Iterable<Entry<Integer, Point>> nearestEntries = localRtree.nearest(myPoint, 100000, k);

        List<LocalEngineResponse.Location> locations = new ArrayList<>();

        for(Entry<Integer, Point> entry : nearestEntries){
            Integer nodeId = entry.value();
            NodeData node = nodeStorage.get(nodeId);

            if (node != null) {

                double distanceMeter = calculateHaversineMeter(request.lat(), request.lon(), node.lat, node.lon);

                double distanceKm = distanceMeter / 1000.0;

                double roundedDistanceKm = Math.round(distanceKm * 100.0) / 100.0;

                locations.add(new LocalEngineResponse.Location(
                        node.nodeId(),
                        roundedDistanceKm,
                        node.lat,
                        node.lon)
                );
            }
        }

        return Mono.just(new LocalEngineResponse(
                        new LocalEngineResponse.MyLocation(request.lat(), request.lon()),
                locations));

    }

    public double calculateHaversineMeter(double lat1, double lon1, double lat2, double lon2) {

        final double R = 6371000;

        // 위도 (lat) 등은 점이 아니라, 이미 각도라고 볼 수 있다.
        // 이를 테면, 지구 중심에서 점까지 뻗어가는 거리 그 자체. 지구가 구에 가까우니 2차원면의 점 하나로 전제 시 (실질적 0 + lat1 거리),
        // x, y축 자체가 하나의 비로 성립하게 되는 거고, radians를 통해서 지구라는 곡면의 최단거리(호)의 곡률을 정의.
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);

        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // 하버사인 핵심 산식
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(phi1) * Math.cos(phi2) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

}

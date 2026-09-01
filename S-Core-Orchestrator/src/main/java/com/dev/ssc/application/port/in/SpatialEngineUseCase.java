package com.dev.ssc.application.port.in;

import com.dev.ssc.application.port.in.dto.SpatialSearchQuery;
import com.dev.ssc.core.dto.NodeData;
import com.dev.ssc.core.dto.SpatialResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SpatialEngineUseCase {
    Mono<SpatialResult> findNearby(SpatialSearchQuery spatialSearchQuery);

//    List<NodeData> ; // 일단 Function을 사용해서 한 번 해보자.
}

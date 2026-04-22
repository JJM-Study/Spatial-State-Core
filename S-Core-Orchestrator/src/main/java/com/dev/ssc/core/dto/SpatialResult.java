package com.dev.ssc.core.dto;


import com.dev.ssc.infrastructure.out.fastapi.dto.NearbyResponse;

import java.util.List;

public record SpatialResult(
        Double centerLat,
        Double centerLon,

        List<NodeInfo> nearbyNodes
) {


        public record NodeInfo (
            int n_id,
            Double distance,
            Double lat,
            Double lon
        ) {}
}
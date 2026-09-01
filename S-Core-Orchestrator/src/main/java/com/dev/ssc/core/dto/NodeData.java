package com.dev.ssc.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

public record NodeData (

        // 개별 상점을 연산으로 다 계산하는 건 불가. 즉, 상가 기준으로
        Double lat,

        Double lon,

        List<MetaNode> metaNodes
        // 이후 상가 기준이 아니라, 좀 더 범용적 추상화 방법이 없을까? 고민.

) {

    public record MetaNode (

    String nodeId,
    String name,
    String categoryLarge,
    String categoryMid,
    String address

    ) {}

}

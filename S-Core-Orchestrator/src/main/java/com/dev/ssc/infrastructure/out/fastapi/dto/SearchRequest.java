package com.dev.ssc.infrastructure.out.fastapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

//public class SearchRequest {
public record SearchRequest (
    @JsonProperty("my_lat") Double myLat,
    @JsonProperty("my_lon") Double myLon,
    Integer k

) {}

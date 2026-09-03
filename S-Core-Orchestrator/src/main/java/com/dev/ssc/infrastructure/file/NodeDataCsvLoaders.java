package com.dev.ssc.infrastructure.file;

import com.dev.ssc.core.dto.NodeData;
import com.dev.ssc.infrastructure.config.RowValues;
import org.apache.logging.log4j.jul.CoreLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

// final : V_loader ← V_subclass 차단
@Configuration
public class NodeDataCsvLoaders {

    record Coordinate(
            Double lon,
            Double lat
    ) {}

    private final CsvNodeLoader csvNodeLoader;

    // CSV 노드 데이터 로더 구현 방향 검토 참조
    public NodeDataCsvLoaders(CsvNodeLoader csvNodeLoader) {
        this.csvNodeLoader = csvNodeLoader;
    }

    // forSpatialNodes의 타입이 CsvNodeLoader를 바라보니까,static과 인스턴스 생성 공존 시 길이 엇나갈 수 밖에.
//    public static CsvNodeLoader<NodeData> forSpatialNodes(Path csvpath) {
    @Bean
    public List<NodeData> forSpatialNodes() {


        List<Map<String, String>> rows;

        String csvPath = "/data/seoul-jung-gu.csv";

        try(InputStream is = getClass().getResourceAsStream(csvPath)) {

            rows = csvNodeLoader.read(is);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 경도, 위도 분류 후 각각에 대해서 stream
        return rows.stream()
                .collect(Collectors.groupingBy(NodeDataCsvLoaders::toCoordinate))
                .entrySet()
                .stream()
                .map(NodeDataCsvLoaders::toNodeData)
                .toList();

    }

    // 하나로 합칠 방법으로는 record가 최선으로 보임.
    private static Coordinate toCoordinate(Map<String,String> row) {
        return new Coordinate(
                RowValues.getDouble(row,"경도"), RowValues.getDouble(row,"위도"));
    }

    private static NodeData toNodeData(Map.Entry<Coordinate, List<Map<String, String>>> entry) {

        // groupby 된 것에 대한 값들의 순회
        return new NodeData(
                entry.getKey().lat,
                entry.getKey().lon,
                entry.getValue().
                        stream().
                        map(NodeDataCsvLoaders::toMetaNode).
                        toList());
    }

    private static NodeData.MetaNode toMetaNode(Map<String, String> row) {

        return new NodeData.MetaNode (
                RowValues.get(row,"상가업소번호"),
                RowValues.get(row,"상호명"),
                RowValues.get(row, "상권업종대분류명"),
                RowValues.get(row, "상권업종중분류명"),
                RowValues.get(row, "도로명주소")
        );
    }
}

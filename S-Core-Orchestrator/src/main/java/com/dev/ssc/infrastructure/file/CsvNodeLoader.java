package com.dev.ssc.infrastructure.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CsvNodeLoader {

//    private final Function<List<CsvRow>, List<T>> converter;

    // {CsvRow, T} = R(Result)
    // V_interface → V_loader, r = CsvRow
    // V_config → V_interface, r = T
    // reach(CsvRow) ￢∩ V_config = ∅
    // 수학적으로는 이런 느낌으로 가도록.
    private final Logger logger = LogManager.getLogger();

    public CsvNodeLoader() {

    }

//    private CsvNodeLoader(Function<List<CsvRow>, List<T>> converter) {
//        this.converter = converter;
//    }

//    private CsvNodeLoader(Function<List<CsvRow>, List<T>> converter) {
//        this.converter = converter;
//    }


//    record CsvRow(Map<String, String> data) {
//        public CsvRow {
//            data = Map.copyOf(data);// 힙 영역 개별 할당 안 되는 상태니 이렇게라도.
//        }
//        public String get(String columnName) {
//            String check = data.get(columnName);
//            if (check == null) {
//                throw new NoSuchElementException("column not found: " + columnName);
//            }
//            return check;
//        }
//        public int getInt(String columnName) {
//
//            return Integer.parseInt(data.get(columnName));
//        }
//        public Double getDouble(String columnName) {
//
//            return Double.parseDouble(data.get(columnName));
//        }
//    }



    //public List<Map<String, String>> read(Path csvPath) {
    public List<Map<String, String>> read(InputStream csvPath) {

        // CsvNodeLoader 구현 방법 참고
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvPath, StandardCharsets.UTF_8)
            ))
            {
            List<String> lines = reader.lines().toList();

            List<String> headers = Arrays.stream(lines.getFirst().split(",", -1))
                    .map(String::trim)
                    .toList();

            return lines.stream()
                    .skip(1)
                    .map(line -> toRow(headers, line))
                    .toList();
        }
         catch (IOException e) {
            throw new RuntimeException("CSV 로드 실패: " + csvPath);
        }
    }

    private Map<String, String> toRow(List<String> headers, String line) {
        String[] cols = line.split(",",-1);
        Map<String, String> data = new LinkedHashMap<>();
        for (int j=0 ; j < headers.size() ; j++) {
            data.put(headers.get(j), j < cols.length ? cols[j].trim() : "");
        }

        return data;
    }

}

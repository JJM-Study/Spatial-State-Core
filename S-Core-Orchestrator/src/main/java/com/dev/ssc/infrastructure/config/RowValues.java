package com.dev.ssc.infrastructure.config;

import com.dev.ssc.core.dto.NodeData;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class RowValues {

    // 메소드 자체는 JVM 실행 시 하나 생성이니까 GC 문제는 없음.
    public static String get(Map<String, String> row, String columnName) {
        String v = row.get(columnName);
        if(v == null) {
            throw new NoSuchElementException("column not found:  " + columnName);
        }

        return v;
    }

    public static int getInt(Map<String, String> row, String columnName) {
       return Integer.parseInt(row.get(columnName));
    }

    public static Double getDouble(Map<String, String> row, String columnName) {
        return Double.parseDouble(row.get(columnName));
    }

}

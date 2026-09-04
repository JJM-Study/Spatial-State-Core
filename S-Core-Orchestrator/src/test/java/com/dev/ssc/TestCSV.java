package com.dev.ssc;

import com.dev.ssc.core.dto.NodeData;


import com.dev.ssc.infrastructure.file.CsvNodeLoader;
import com.dev.ssc.infrastructure.file.NodeDataCsvLoaders;
import org.junit.jupiter.api.Test;
//import org.junit.platform.commons.logging.Logger;
//import org.junit.platform.commons.logging.LoggerFactory;
//import org.apache.logging.log4j.LogManager;
//import org.junit.platform.commons.logging.Logger;
//import org.junit.platform.commons.logging.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class TestCSV {

    @Autowired
    private final List<NodeData> forSpatialNodes;

//    private static final Logger logger = LoggerFactory.getLogger(com.dev.ssc.TestCSV.class);
    private static final Logger logger = LoggerFactory.getLogger(TestCSV.class);

    @Autowired
    TestCSV(List<NodeData> forSpatialNodes) {
        this.forSpatialNodes = forSpatialNodes;

    }


    @Test
    public void read() {

//        CsvNodeLoader csvNodeLoader = new CsvNodeLoader();
//        NodeDataCsvLoaders nodeDataCsvLoaders = new N

       logger.info("com.dev.ssc.TestCSV Return :" + forSpatialNodes);

    }

}

import com.dev.ssc.core.dto.NodeData;
import com.dev.ssc.infrastructure.file.CsvNodeLoader;
import com.dev.ssc.infrastructure.file.NodeDataCsvLoaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;

import java.nio.file.Path;


class TestCSV {

    @Autowired
    private CsvNodeLoader csvNodeLoader;

    @Autowired
    private NodeDataCsvLoaders nodeDataCsvLoaders;


    public List<NodeData> read() {
        return nodeDataCsvLoaders.forSpatialNodes();


    }

}

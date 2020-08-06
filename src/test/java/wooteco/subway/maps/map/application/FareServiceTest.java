package wooteco.subway.maps.map.application;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;
import wooteco.subway.common.TestObjectUtils;
import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.domain.LineStation;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.domain.SubwayPath;
import wooteco.subway.maps.station.domain.Station;

@ExtendWith(MockitoExtension.class)
public class FareServiceTest {
    private List<Line> lines;
    private PathService pathService;
    private FareService fareService;

    @BeforeEach
    void setUp() {
        Map<Long, Station> stations = new HashMap<>();
        stations.put(1L, TestObjectUtils.createStation(1L, "A역"));
        stations.put(2L, TestObjectUtils.createStation(2L, "B역"));
        stations.put(3L, TestObjectUtils.createStation(3L, "C역"));
        stations.put(4L, TestObjectUtils.createStation(4L, "D역"));

        Line line1 = TestObjectUtils.createLine(1L, "새로운선", "GREEN");
        line1.addLineStation(new LineStation(1L, null, 0, 0));
        line1.addLineStation(new LineStation(2L, 1L, 20, 3));
        line1.addLineStation(new LineStation(3L, 2L, 40, 5));
        line1.addLineStation(new LineStation(4L, 3L, 60, 8));

        Line line2 = TestObjectUtils.createLine(2L, "새새로운선", "RED");
        line2.addLineStation(new LineStation(2L, null, 0, 0));
        line2.addLineStation(new LineStation(4L, 2L, 10, 30));

        lines = Lists.newArrayList(line1, line2);

        pathService = new PathService();
        fareService = new FareService();
    }

    @Test
    void calculateByDistance() {
        SubwayPath distancePath = pathService.findPath(lines, 1L, 4L, PathType.DISTANCE);
        SubwayPath durationPath = pathService.findPath(lines, 1L, 4L, PathType.DURATION);
        assertThat(fareService.calculateByDistance(distancePath.calculateDistance())).isEqualTo(400);
        assertThat(fareService.calculateByDistance(durationPath.calculateDistance())).isEqualTo(1600);
    }
}

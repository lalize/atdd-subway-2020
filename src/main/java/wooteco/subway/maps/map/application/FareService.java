package wooteco.subway.maps.map.application;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.maps.line.application.LineService;
import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.map.domain.LineStationEdge;
import wooteco.subway.maps.map.domain.SubwayPath;

@Service
public class FareService {
    private static final int DEFAULT_FARE = 1250;

    private LineService lineService;

    public FareService(LineService lineService) {
        this.lineService = lineService;
    }

    public int calculate(SubwayPath subwayPath, int age) {
        int distance = subwayPath.calculateDistance();
        List<LineStationEdge> lineStationEdges = subwayPath.getLineStationEdges();
        return calculateByAge(
            DEFAULT_FARE + calculateByDistance(distance) + calculateByExtraFare(distance, lineStationEdges), age);
    }

    public int calculateByDistance(int distance) {
        int fare = 0;
        if (distance > 10) {
            fare += Math.ceil((Math.min(distance, 50) - 10) / 5) * 100;
        }
        if (distance > 50) {
            fare += Math.ceil((distance - 50) / 8) * 100;
        }
        return fare;
    }

    public int calculateByExtraFare(int distance, List<LineStationEdge> lineStationEdges) {
        if (distance < 8) {
            return 0;
        }

        List<Long> lineIds = lineStationEdges.stream().map(LineStationEdge::getLineId).collect(Collectors.toList());

        return lineService.findLines()
            .stream()
            .filter(line -> lineIds.contains(line.getId()))
            .max(Comparator.comparingInt(Line::getExtraFare))
            .orElseThrow(RuntimeException::new)
            .getExtraFare();
    }

    public int calculateByAge(int fare, int age) {
        if (age >= 19) {
            return fare;
        }
        if (age >= 13) {
            return (int)((fare - 350) * 0.8);
        }
        if (age >= 6) {
            return (int)((fare - 350) * 0.5);
        }
        return 0;
    }
}

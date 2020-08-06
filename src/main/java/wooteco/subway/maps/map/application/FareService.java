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
    private static final int FIRST_OVER_FARE_START = 10;
    private static final int FIRST_OVER_FARE_RANGE = 5;
    private static final int SECOND_OVER_FARE_START = 50;
    private static final int SECOND_OVER_FARE_RANGE = 8;
    private static final int EXTRA_FARE = 100;
    private static final int LINE_EXTRA_FARE_RANGE = 8;
    private static final int DEDUCTION = 350;
    private static final int CHILDREN = 6;
    private static final int YOUTH = 13;
    private static final int ADULT = 19;
    private static final double CHILDREN_DISCOUNT_RATE = 0.5;
    private static final double YOUTH_DISCOUNT_RATE = 0.8;

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
        if (distance > FIRST_OVER_FARE_START) {
            fare +=
                Math.ceil((Math.min(distance, SECOND_OVER_FARE_START) - FIRST_OVER_FARE_START) / FIRST_OVER_FARE_RANGE)
                    * EXTRA_FARE;
        }
        if (distance > SECOND_OVER_FARE_START) {
            fare += Math.ceil((distance - SECOND_OVER_FARE_START) / SECOND_OVER_FARE_RANGE) * EXTRA_FARE;
        }
        return fare;
    }

    public int calculateByExtraFare(int distance, List<LineStationEdge> lineStationEdges) {
        if (distance < LINE_EXTRA_FARE_RANGE) {
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
        if (age >= ADULT) {
            return fare;
        }
        if (age >= YOUTH) {
            return (int)((fare - DEDUCTION) * YOUTH_DISCOUNT_RATE);
        }
        if (age >= CHILDREN) {
            return (int)((fare - DEDUCTION) * CHILDREN_DISCOUNT_RATE);
        }
        return 0;
    }
}

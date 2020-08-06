package wooteco.subway.maps.map.application;

import org.springframework.stereotype.Service;

@Service
public class FareService {
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
}

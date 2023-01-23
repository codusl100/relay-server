package com.example.relayRun.record.util;

import com.example.relayRun.record.dto.locationDTO;
import com.example.relayRun.record.entity.LocationEntity;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RecordDataHandler {
    public static Float toSecond(LocalTime time) {
        return time.getHour() * 3600 +
                time.getMinute() * 60 +
                time.getSecond() +
                time.getNano() / 1000f;
    }
    public static Point toPoint(Float longitude, Float latitude) throws ParseException {
        String pointWKT = String.format("POINT(%s %s)", longitude, latitude);
        Point point = (Point) new WKTReader().read(pointWKT);
        return point;
    }

    public static List<LocationEntity> toEntityList(List<locationDTO> locations) throws ParseException {
        ArrayList<LocationEntity> entities = new ArrayList<>();
        for (locationDTO location : locations) {
            LocationEntity entity = LocationEntity.builder()
                    .time(LocalDateTime.parse(
                            location.getTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    )
                    .position(toPoint(location.getLongitude(), location.getLatitude()))
                    .status(location.getStatus())
                    .build();
            entities.add(entity);
        }
        return entities;
    }
}

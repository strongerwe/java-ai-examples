package com.stronger.ai;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ZoneUtils.class
 * @department Platform R&D
 * @date 2025/7/26
 * @desc do what?
 */
public class ZoneUtils {
    public static String getTimeByZoneId(String zoneId) {
        ZoneId zid = ZoneId.of(zoneId);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return zonedDateTime.format(formatter);
    }
}

package com.talentboozt.s_backend.Service._private;

import com.talentboozt.s_backend.Shared.IpGeoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TimeZoneMismatchService {

    @Autowired
    private IpTimeZoneService ipTimeZoneService;

    public boolean isTimeZoneMismatch(String ipAddress, int clientOffsetMinutes) {
        IpGeoData ipTimeZone = ipTimeZoneService.getTimeZoneForIp(ipAddress);

        if (ipTimeZone == null) {
            return false;
        }

        if ("Unknown".equals(ipTimeZone.getTimezone())) {
            return false; // fallback or flag separately
        }

        ZoneId zoneId = ZoneId.of(ipTimeZone.getTimezone());
        int serverOffsetMinutes = ZonedDateTime.now(zoneId).getOffset().getTotalSeconds() / 60;

        return Math.abs(serverOffsetMinutes + clientOffsetMinutes) > 5;
        // Note: client offset is NEGATIVE behind UTC (e.g., UTC+5:30 = -330)
    }
}


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
            return true;
        }

        if ("Unknown".equals(ipTimeZone.getTimezone())) {
            return true; // fallback or flag separately
        }

        ZoneId zoneId = ZoneId.of(ipTimeZone.getTimezone());
        int serverOffsetMinutes = ZonedDateTime.now(zoneId).getOffset().getTotalSeconds() / 60;

        int clientOffsetAbs = Math.abs(clientOffsetMinutes); // Client sends negative for UTC+
        int difference = Math.abs(serverOffsetMinutes - clientOffsetAbs);
        return difference > 5;
        // Note: clientOffsetMinutes is negative when ahead of UTC (e.g., UTC+5:30 = -330)
    }
}


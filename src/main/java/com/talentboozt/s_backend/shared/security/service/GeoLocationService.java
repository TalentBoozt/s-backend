package com.talentboozt.s_backend.shared.security.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeoLocationService {

    @Autowired
    ConfigUtility configUtility;

    private final OkHttpClient httpClient;

    public GeoLocationService() {
        // Configure OkHttpClient with connection pooling and timeouts to prevent resource leaks
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .connectionPool(new okhttp3.ConnectionPool(10, 5, java.util.concurrent.TimeUnit.MINUTES))
                .build();
    }

    public Map<String, String> getGeoLocation(String ipAddress) throws Exception {
        String url = "https://ipinfo.io/" + ipAddress + "/json?token=" + configUtility.getProperty("IPINFO_TOKEN");
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("Unexpected code " + response);

            assert response.body() != null;
            JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
            Map<String, String> location = new HashMap<>();
            location.put("ip", json.get("ip").getAsString());
            location.put("city", json.get("city").getAsString());
            location.put("region", json.get("region").getAsString());
            location.put("country", json.get("country").getAsString());
            location.put("loc", json.get("loc").getAsString());
            location.put("org", json.get("org").getAsString());
            return location;
        }
    }
}



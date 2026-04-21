package com.talentboozt.s_backend.domains.edu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduDataService {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public List<Map<String, Object>> listAvailableCollections() {
        return mongoTemplate.getCollectionNames().stream()
                .filter(name -> name.startsWith("edu_"))
                .map(name -> {
                    Map<String, Object> info = new java.util.HashMap<>();
                    info.put("id", name);
                    info.put("label", formatLabel(name));
                    info.put("count", mongoTemplate.count(new Query(), name));
                    return info;
                })
                .collect(Collectors.toList());
    }

    private String formatLabel(String name) {
        String clean = name.replace("edu_", "").replace("_", " ");
        return clean.substring(0, 1).toUpperCase() + clean.substring(1);
    }

    public String exportCollectionToJson(String collectionName) {
        try {
            List<Map> data = mongoTemplate.find(new Query(), Map.class, collectionName);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (Exception e) {
            log.error("Failed to export collection {}: {}", collectionName, e.getMessage());
            throw new RuntimeException("Export failed", e);
        }
    }

    public String exportCollectionToCsv(String collectionName) {
        try {
            List<Map> data = mongoTemplate.find(new Query(), Map.class, collectionName);
            if (data.isEmpty()) return "";

            // Get all unique keys for headers
            List<String> headers = data.stream()
                    .flatMap(m -> ((Map<String, Object>) m).keySet().stream())
                    .distinct()
                    .collect(Collectors.toList());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            // Write headers
            pw.println(String.join(",", headers));

            // Write rows
            for (Map m : data) {
                String row = headers.stream()
                        .map(h -> {
                            Object val = m.get(h);
                            if (val == null) return "";
                            String s = val.toString().replace("\"", "\"\"");
                            return "\"" + s + "\"";
                        })
                        .collect(Collectors.joining(","));
                pw.println(row);
            }

            return sw.toString();
        } catch (Exception e) {
            log.error("Failed to export collection to CSV {}: {}", collectionName, e.getMessage());
            throw new RuntimeException("Export failed", e);
        }
    }
}

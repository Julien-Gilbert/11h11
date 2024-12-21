package com._h11.api.controller;

import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DailyPictureController {

    private final Map<String, Map<String, String>> imageMetadata;

    public DailyPictureController() {
        this.imageMetadata = loadImageMetadata();
    }

    @GetMapping("/dailyPictureMetadata")
    public Map<String, Object> getDailyPictureMetadata() {
        String todayKey = getKeyForCurrentTime();
        Map<String, String> metadata = imageMetadata.getOrDefault(todayKey, defaultMetadata(todayKey));

        Map<String, Object> response = new HashMap<>();
        response.put("targetXPercent", Integer.parseInt(metadata.get("targetXPercent")));
        response.put("targetYPercent", Integer.parseInt(metadata.get("targetYPercent")));
        response.put("name", metadata.get("name"));
        response.put("imageUrl", "http://api.labatailledutrain.fr/" + metadata.get("imageName"));
        return response;
    }

    private String getKeyForCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();
        LocalTime startOfPeriod = LocalTime.of(11, 11);

        if (currentTime.isBefore(startOfPeriod)) {
            currentDate = currentDate.minusDays(1);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return currentDate.format(formatter);
    }

    private Map<String, String> defaultMetadata(String key) {
        Map<String, String> defaultData = new HashMap<>();
        defaultData.put("imageName", key + "-Default.jpg");
        defaultData.put("targetXPercent", "50");
        defaultData.put("targetYPercent", "50");
        defaultData.put("name", "Default Image");
        return defaultData;
    }

    
    private Map<String, Map<String, String>> loadImageMetadata() {
        Map<String, Map<String, String>> metadataMap = new HashMap<>();
        Properties properties = new Properties();

        // Chemin externe configurable
        String externalConfigPath = "/var/www/clients/client0/web4/home/defaultJulienGilbert/web/11h11/config/imageMappings.properties";

        try (FileInputStream fis = new FileInputStream(externalConfigPath)) {
            properties.load(fis);
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                Map<String, String> metadata = parseMetadata(value);
                metadataMap.put(key, metadata);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Impossible de charger le fichier externe : " + externalConfigPath);
        }
        return metadataMap;
    }

    private Map<String, String> parseMetadata(String value) {
        Map<String, String> metadata = new HashMap<>();
        String[] parts = value.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                metadata.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return metadata;
    }
}


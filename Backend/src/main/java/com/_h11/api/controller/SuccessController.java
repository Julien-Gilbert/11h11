package com._h11.api.controller;

import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SuccessController {

    private final String filePath = "counter.txt";
    private final ReentrantLock lock = new ReentrantLock();

    @GetMapping("/success")
    public Map<String, Integer> incrementCounter() {
        lock.lock();
        
        Map<String, Integer> response = new HashMap<>();
        
        try {
            int currentValue = readCounterFromFile();
            int newValue = currentValue + 1;
            writeCounterToFile(newValue);
            
            response.put("rank", newValue);
            
            return response;
        } finally {
            lock.unlock();
        }
    }

    private int readCounterFromFile() {
        try {
            if (!Files.exists(Paths.get(filePath))) {
                Files.write(Paths.get(filePath), "0".getBytes());
            }
            String content = new String(Files.readAllBytes(Paths.get(filePath))).trim();
            return Integer.parseInt(content);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void writeCounterToFile(int value) {
        try {
            Files.write(Paths.get(filePath), String.valueOf(value).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


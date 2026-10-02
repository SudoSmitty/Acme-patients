package com.acme.patients.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InfoController {

    @Value("${spring.application.name}")
    private String app;
    @Value("${acme.version}")
    private String version;
    @Value("${acme.environment}")
    private String environment;
    @Value("${acme.track}")
    private String track;
    @Value("${acme.pod-name}")
    private String pod;

    @GetMapping("/api/info")
    public Map<String, String> info() {
        return Map.of("app", app, "version", version, "environment", environment, "track", track, "pod", pod);
    }
}

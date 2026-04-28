package com.dbb.controller;

import com.dbb.entity.PlatformSettings;
import com.dbb.repository.PlatformSettingsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/platform-settings")
public class PlatformSettingsController {

    private final PlatformSettingsRepository settingsRepo;

    public PlatformSettingsController(PlatformSettingsRepository settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    @GetMapping
    public PlatformSettings getSettings() {
        return settingsRepo.findById(1).orElseGet(() -> {
            PlatformSettings s = new PlatformSettings();
            return settingsRepo.save(s);
        });
    }

    @PutMapping
    public PlatformSettings updateSettings(@RequestBody Map<String, Object> body) {
        PlatformSettings s = settingsRepo.findById(1).orElse(new PlatformSettings());

        if (body.containsKey("platformName")) s.setPlatformName((String) body.get("platformName"));
        if (body.containsKey("contactEmail")) s.setContactEmail((String) body.get("contactEmail"));
        if (body.containsKey("contactPhone")) s.setContactPhone((String) body.get("contactPhone"));
        if (body.containsKey("aboutText")) s.setAboutText((String) body.get("aboutText"));
        
        if (body.containsKey("maxStudents")) {
            Object val = body.get("maxStudents");
            if (val instanceof Integer) s.setMaxStudents((Integer) val);
            else if (val instanceof String) s.setMaxStudents(Integer.parseInt((String) val));
        }

        if (body.containsKey("publicCourses")) s.setPublicCourses((Boolean) body.get("publicCourses"));
        if (body.containsKey("guestEnroll")) s.setGuestEnroll((Boolean) body.get("guestEnroll"));
        if (body.containsKey("maintenanceMode")) s.setMaintenanceMode((Boolean) body.get("maintenanceMode"));
        if (body.containsKey("autoGrade")) s.setAutoGrade((Boolean) body.get("autoGrade"));
        if (body.containsKey("certificatesEnabled")) s.setCertificatesEnabled((Boolean) body.get("certificatesEnabled"));

        return settingsRepo.save(s);
    }
}

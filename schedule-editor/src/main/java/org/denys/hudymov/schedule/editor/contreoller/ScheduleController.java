package org.denys.hudymov.schedule.editor.contreoller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Schedule")
public interface ScheduleController {
    @PostMapping("/schedule/compose")
    ResponseEntity<FileSystemResource> composeScheduleFromPlan(
            @RequestParam("schedule_file") MultipartFile scheduleFile,
            @RequestParam("education_file") MultipartFile educationFile
    ) throws IOException;

    @PostMapping("/schedule/review")
    ResponseEntity<FileSystemResource> reviewSchedule(@RequestParam("schedule_file") MultipartFile scheduleFile) throws IOException;
}

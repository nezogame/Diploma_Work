package org.denys.hudymov.schedule.editor.contreoller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.denys.hudymov.schedule.editor.domain.ScheduleAnalyseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Schedule")
public interface ScheduleController {
    @PostMapping("/upload")
    ResponseEntity<Void> uploadFile(@RequestParam("schedule_file") MultipartFile scheduleFile,
                                    @RequestParam("education_file")MultipartFile educationFile);

    @PostMapping("/schedule/review")
    ResponseEntity<ScheduleAnalyseResponse> reviewSchedule(@RequestParam("schedule_file") MultipartFile scheduleFile) throws IOException;
}

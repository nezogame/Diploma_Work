package org.denys.hudymov.schedule.editor.contreoller.impl;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.denys.hudymov.schedule.editor.contreoller.ScheduleController;
import org.denys.hudymov.schedule.editor.domain.ScheduleAnalyseResponse;
import org.denys.hudymov.schedule.editor.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class ScheduleControllerImpl implements ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleControllerImpl(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public ResponseEntity<Void> uploadFile(MultipartFile scheduleFile, MultipartFile educationFile) {
        scheduleService.uploadFile(scheduleFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ScheduleAnalyseResponse> reviewSchedule(MultipartFile scheduleFile) throws IOException {

        return ResponseEntity.ok(scheduleService.reviewSchedule(scheduleFile));
    }
}

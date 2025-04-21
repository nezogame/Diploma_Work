package org.denys.hudymov.schedule.editor.contreoller.impl;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.denys.hudymov.schedule.editor.contreoller.ScheduleController;
import org.denys.hudymov.schedule.editor.service.ScheduleService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    public ResponseEntity<FileSystemResource> composeScheduleFromPlan(MultipartFile scheduleFile, MultipartFile educationFile) throws IOException {
        var formattedSchedule = scheduleService.populateSchedule(scheduleFile, educationFile);

        var filename = formattedSchedule.getFilename();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Disposition",
                "attachment;filename=" + filename);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(responseHeaders)
                .body(formattedSchedule);
    }

    @Override
    public ResponseEntity<FileSystemResource> reviewSchedule(MultipartFile scheduleFile) throws IOException {
        var optionalScheduleUpdatedResponse = scheduleService.reviewSchedule(scheduleFile);
        if (optionalScheduleUpdatedResponse.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var filename = optionalScheduleUpdatedResponse.get().getFilename();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Disposition",
                "attachment;filename=" + filename);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(responseHeaders)
                .body(optionalScheduleUpdatedResponse.get());
    }
}

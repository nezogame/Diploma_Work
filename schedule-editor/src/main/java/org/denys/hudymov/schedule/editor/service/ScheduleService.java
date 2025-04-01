package org.denys.hudymov.schedule.editor.service;

import java.io.IOException;
import java.util.Optional;
import org.denys.hudymov.schedule.editor.domain.ScheduleAnalyseResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ScheduleService {
    Optional<String> uploadFile(MultipartFile file);

    ScheduleAnalyseResponse reviewSchedule(MultipartFile file) throws IOException;
}


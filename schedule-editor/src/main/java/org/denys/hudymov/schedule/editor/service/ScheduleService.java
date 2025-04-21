package org.denys.hudymov.schedule.editor.service;

import java.io.IOException;
import java.util.Optional;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

public interface ScheduleService {
    Optional<String> uploadFile(MultipartFile file);

    Optional<FileSystemResource> reviewSchedule(MultipartFile file) throws IOException;

    FileSystemResource populateSchedule(MultipartFile file, MultipartFile educationalProgramFile) throws IOException;
}


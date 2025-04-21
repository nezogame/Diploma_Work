package org.denys.hudymov.schedule.editor.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.denys.hudymov.schedule.editor.service.ExcelCreatorService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ExcelCreatorServiceImpl implements ExcelCreatorService {
    public static final String MODIFIED_SCHEDULE = "modified-schedule";

    @Override
    public FileSystemResource createChangedExcelFile(Workbook workbook, MultipartFile file) throws IOException {
        String filename = Objects.requireNonNull(file.getOriginalFilename());

        var fileExtension = getFileExtension(filename);

        return createTempExcelFile(workbook, fileExtension);
    }

    private String getFileExtension(String filename) {
        var startOfExtension = filename.lastIndexOf(".");

        if (startOfExtension == -1) {
            throw new IllegalArgumentException("File name is empty, or has no extension");
        }

        return filename.substring(startOfExtension);
    }

    private static FileSystemResource createTempExcelFile(Workbook workbook, String extension) throws IOException {
        File tempFile = File.createTempFile(MODIFIED_SCHEDULE, extension);
        try (var fileOut = Files.newOutputStream(tempFile.toPath())) {
            workbook.write(fileOut);
        }

        var modifiedSchedule = new FileSystemResource(tempFile);
        tempFile.deleteOnExit();
        log.debug("Absolute path to modified schedule file: {}", modifiedSchedule.getFile().getAbsolutePath());
        return modifiedSchedule;
    }
}

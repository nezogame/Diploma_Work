package org.denys.hudymov.schedule.editor.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import org.denys.hudymov.schedule.editor.domain.ScheduleAnalyseResponse;
import org.denys.hudymov.schedule.editor.llm.assistant.ScheduleAssistant;
import org.denys.hudymov.schedule.editor.service.ExcelIOService;
import org.denys.hudymov.schedule.editor.service.ScheduleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private final ScheduleAssistant scheduleAssistant;
    private final ExcelIOService excelIOService;
    @Value("${save.folder}")
    private String saveFolder;

    public ScheduleServiceImpl(ScheduleAssistant scheduleAssistant, ExcelIOService excelIOService) {
        this.scheduleAssistant = scheduleAssistant;
        this.excelIOService = excelIOService;
    }


    @Override
    public Optional<String> uploadFile(MultipartFile file) {
        String scheduleFile = null;

        try {
            scheduleFile = saveScheduleFile(file);
            log.info("File uploaded successfully!");
        } catch (IOException e) {
            log.error("File upload failed!", e);
        }

        return Optional.ofNullable(scheduleFile);
    }

    @Override
    public ScheduleAnalyseResponse reviewSchedule(MultipartFile scheduleFile) throws IOException {
        var scheduleTemplate = excelIOService.readExcelFile(scheduleFile);
        var scheduleOwerviewResult = scheduleAssistant.generateSchedule(scheduleTemplate);
        log.info("Tokens was used[{}], finish reason[{}]:, sources[{}]",
                scheduleOwerviewResult.tokenUsage(),
                scheduleOwerviewResult.finishReason(),
                scheduleOwerviewResult.sources()

        );
        return scheduleOwerviewResult.content();
    }


    private String saveScheduleFile(MultipartFile file) throws IOException {
        String dirPath = findCurrentPath();
        createFolder(dirPath);
        File uploadedFile = saveFile(file, dirPath);
        return uploadedFile.getAbsolutePath();
    }

    private static File saveFile(MultipartFile file, String dirPath) throws IOException {
        String fileName = file.getOriginalFilename();
        File uploadedFile = new File(dirPath, fileName);
        file.transferTo(uploadedFile);
        return uploadedFile;
    }

    private void createFolder(String dirPath) throws IOException {
        var destFolder = new File(dirPath + File.separator + saveFolder);
        Files.createDirectory(destFolder.toPath());
    }

    private String findCurrentPath() {
        return Paths.get("").toAbsolutePath().toString();
    }
}

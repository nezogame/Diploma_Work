package org.denys.hudymov.schedule.editor.service.impl;

import dev.langchain4j.service.Result;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.denys.hudymov.schedule.editor.domain.ScheduleAnalyseDto;
import org.denys.hudymov.schedule.editor.llm.assistant.ScheduleAssistant;
import org.denys.hudymov.schedule.editor.service.ExcelCreatorService;
import org.denys.hudymov.schedule.editor.service.ExcelIOService;
import org.denys.hudymov.schedule.editor.service.ScheduleService;
import org.denys.hudymov.schedule.editor.utils.factory.ExcelWorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleAssistant scheduleAssistant;
    private final ExcelIOService excelIOService;
    private final ExcelCreatorService excelCreatorService;
    @Value("${save.folder}")
    private String saveFolder;

    public ScheduleServiceImpl(ScheduleAssistant scheduleAssistant, ExcelIOService excelIOService,
                               ExcelCreatorService excelCreatorService
    ) {
        this.scheduleAssistant = scheduleAssistant;
        this.excelIOService = excelIOService;
        this.excelCreatorService = excelCreatorService;
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

    private String saveScheduleFile(MultipartFile file) throws IOException {
        String dirPath = findCurrentPath();
        createFolder(dirPath);
        File uploadedFile = saveFile(file, dirPath);
        return uploadedFile.getAbsolutePath();
    }

    private String findCurrentPath() {
        return Paths.get("").toAbsolutePath().toString();
    }

    private void createFolder(String dirPath) throws IOException {
        var destFolder = new File(dirPath + File.separator + saveFolder);
        Files.createDirectory(destFolder.toPath());
    }

    private static File saveFile(MultipartFile file, String dirPath) throws IOException {
        String fileName = file.getOriginalFilename();
        File uploadedFile = new File(dirPath, Objects.requireNonNull(fileName));
        file.transferTo(uploadedFile);
        return uploadedFile;
    }

    @Override
    public Optional<FileSystemResource> reviewSchedule(MultipartFile scheduleFile) throws IOException {
        var schedule = excelIOService.readExcelFile(scheduleFile);
        var scheduleOwerviewResult = scheduleAssistant.generateSchedule(schedule);

        logModelResources(scheduleOwerviewResult);

        var analyseContent = scheduleOwerviewResult.content();

        if (isNotUpdateNeeded(analyseContent)) {
            return Optional.empty();
        }

        FileSystemResource updateScheduleFile = updateScheduleFile(scheduleFile, analyseContent);
        return Optional.ofNullable(updateScheduleFile);
    }

    private boolean isNotUpdateNeeded(ScheduleAnalyseDto analyseContent) {
        return analyseContent.changedSubjects().isEmpty();
    }

    private FileSystemResource updateScheduleFile(
            MultipartFile scheduleFile,
            ScheduleAnalyseDto scheduleOverviewResult
    ) throws IOException {

        var filename = Objects.requireNonNull(scheduleFile.getOriginalFilename());
        try (InputStream inputStream = scheduleFile.getInputStream();
             Workbook workbook = ExcelWorkbookFactory.createWorkbook(filename, inputStream)) {
            excelIOService.applyChangesToExcelFile(workbook, scheduleOverviewResult.changedSubjects());
            var changedExcelFile = excelCreatorService.createChangedExcelFile(workbook, scheduleFile);
            log.info("Analyzes comment: {}", scheduleOverviewResult.analyseResult());

            return changedExcelFile;
        }
    }

    @Override
    public FileSystemResource populateSchedule(MultipartFile file, MultipartFile educationalProgramFile) throws IOException {
        var schedule = excelIOService.readExcelFile(file);
        var educationalProgram = excelIOService.readExcelFile(educationalProgramFile);
        var scheduleOwerviewResult = scheduleAssistant.generateSchedule(schedule, educationalProgram);

        logModelResources(scheduleOwerviewResult);

        var analyseContent = scheduleOwerviewResult.content();

        return updateScheduleFile(file, analyseContent);
    }

    private static void logModelResources(Result<ScheduleAnalyseDto> scheduleOwerviewResult) {
        log.info("Tokens was used[{}], finish reason[{}]:, sources[{}], tools used {}",
                scheduleOwerviewResult.tokenUsage(),
                scheduleOwerviewResult.finishReason(),
                scheduleOwerviewResult.sources(),
                scheduleOwerviewResult.toolExecutions()
        );
    }
}

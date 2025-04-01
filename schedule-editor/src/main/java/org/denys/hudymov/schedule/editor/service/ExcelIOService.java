package org.denys.hudymov.schedule.editor.service;

import java.io.IOException;
import java.util.Map;
import org.denys.hudymov.schedule.editor.domain.SheetDto;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelIOService {
    Map<String, SheetDto> readExcelFile(MultipartFile file) throws IOException;

    ResponseEntity<FileSystemResource> generateExcelFile();
}

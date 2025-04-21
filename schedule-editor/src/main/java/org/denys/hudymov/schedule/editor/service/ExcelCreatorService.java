package org.denys.hudymov.schedule.editor.service;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelCreatorService {
    FileSystemResource createChangedExcelFile(Workbook workbook, MultipartFile file) throws IOException;

}

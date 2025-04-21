package org.denys.hudymov.schedule.editor.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;
import org.denys.hudymov.schedule.editor.domain.ChangedSubjectDto;
import org.denys.hudymov.schedule.editor.domain.SheetDto;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelIOService {
    Map<String, SheetDto> readExcelFile(MultipartFile file) throws IOException;

    void applyChangesToExcelFile(Workbook file, Map<String, List<ChangedSubjectDto>> changes);

}

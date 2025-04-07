package org.denys.hudymov.schedule.editor.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.denys.hudymov.schedule.editor.domain.CellDto;
import org.denys.hudymov.schedule.editor.domain.RowDto;
import org.denys.hudymov.schedule.editor.domain.SheetDto;
import org.denys.hudymov.schedule.editor.service.ExcelIOService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.denys.hudymov.schedule.editor.factory.ExcelWorkbookFactory.createWorkbook;

@Service
public class ExcelIOServiceImpl implements ExcelIOService {
    @Override
    public Map<String, SheetDto> readExcelFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Map<String, SheetDto> sheetData;

        try (
                InputStream is = file.getInputStream();
                Workbook workbook = createWorkbook(fileName, is)
        ) {
            sheetData = StreamSupport.stream(workbook.spliterator(), false)
                    .collect(Collectors.toMap(
                            Sheet::getSheetName,
                            sheet -> SheetDto.builder()
                                    .rowData(processSheet(sheet))
                                    .build(),
                            (oldValue, newValue) -> SheetDto.builder()
                                    .rowData(
                                            Stream.concat(oldValue.rowData().stream(), newValue.rowData().stream())
                                                    .collect(Collectors.toList())
                                    )
                                    .build(),
                            LinkedHashMap::new
                    ));
        }

        return sheetData;
    }

    private List<RowDto> processSheet(Sheet sheet) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .map(row -> RowDto.builder()
                        .cells(processRow(row))
                        .build()
                ).toList();
    }

    private List<CellDto> processRow(Row row) {
        return StreamSupport.stream(row.spliterator(), false)
                .map(cell -> CellDto.builder()
                        .cellStyle(cell.getCellStyle())
                        .data(cell.toString())
                        .build()
                ).toList();
    }

    @Override
    public ResponseEntity<FileSystemResource> generateExcelFile() {
        return null;
    }
}

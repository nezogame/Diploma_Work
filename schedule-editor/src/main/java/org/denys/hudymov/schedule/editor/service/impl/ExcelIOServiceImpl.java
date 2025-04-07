package org.denys.hudymov.schedule.editor.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
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

import static org.denys.hudymov.schedule.editor.utils.excel.MergedRegionUtil.setCellInMergedRegion;
import static org.denys.hudymov.schedule.editor.utils.factory.ExcelWorkbookFactory.createWorkbook;

@Service
public class ExcelIOServiceImpl implements ExcelIOService {
    @Override
    public Map<String, SheetDto> readExcelFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Map<String, SheetDto> sheetData;

        try (
                InputStream is = file.getInputStream();
                Workbook workbook = createWorkbook(Objects.requireNonNull(fileName), is)
        ) {
            sheetData = StreamSupport.stream(workbook.spliterator(), false)
                    .collect(collectRowsIntoSheetMap());
        }

        return sheetData;
    }

    private Collector<Sheet, ?, LinkedHashMap<String, SheetDto>> collectRowsIntoSheetMap() {
        return Collectors.toMap(
                Sheet::getSheetName,
                sheet -> SheetDto.builder()
                        .rowData(processSheet(sheet))
                        .build(),
                (oldValue, newValue) -> SheetDto.builder().rowData(
                                Stream.concat(oldValue.rowData().stream(), newValue.rowData().stream())
                                        .toList()
                        )
                        .build(),
                LinkedHashMap::new);
    }

    private List<RowDto> processSheet(Sheet sheet) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .map(row -> RowDto.builder()
                        .cells(processRow(sheet, row))
                        .build()
                )
                .toList();
    }

    private List<CellDto> processRow(Sheet sheet, Row row) {
        return StreamSupport.stream(row.spliterator(), false)
                .map(cell -> setCellInMergedRegion(sheet, cell))
                .toList();
    }

    @Override
    public ResponseEntity<FileSystemResource> generateExcelFile() {
        return null;
    }
}

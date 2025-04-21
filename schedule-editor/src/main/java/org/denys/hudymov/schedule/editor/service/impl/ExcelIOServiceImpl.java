package org.denys.hudymov.schedule.editor.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.denys.hudymov.schedule.editor.domain.CellDto;
import org.denys.hudymov.schedule.editor.domain.ChangedSubjectDto;
import org.denys.hudymov.schedule.editor.domain.CoordinatesDto;
import org.denys.hudymov.schedule.editor.domain.MergedRegion;
import org.denys.hudymov.schedule.editor.domain.RowDto;
import org.denys.hudymov.schedule.editor.domain.SheetDto;
import org.denys.hudymov.schedule.editor.service.ExcelIOService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.denys.hudymov.schedule.editor.llm.tool.MergedRegionTool.isRegionInsertable;
import static org.denys.hudymov.schedule.editor.llm.tool.MergedRegionTool.isSingleCellMergedRegion;
import static org.denys.hudymov.schedule.editor.utils.excel.MergedRegionUtil.setCellInMergedRegion;
import static org.denys.hudymov.schedule.editor.utils.factory.ExcelWorkbookFactory.createWorkbook;

@Slf4j
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
    public void applyChangesToExcelFile(Workbook workbook, Map<String, List<ChangedSubjectDto>> changes) {
        changes.forEach(
                (sheetName, change) -> applyChangeToEverySheet(workbook, sheetName, change)
        );
    }

    private void applyChangeToEverySheet(Workbook workbook, String sheetName, List<ChangedSubjectDto> changes) {
        Sheet sheet = workbook.getSheet(sheetName);

        if (sheet == null) {
            log.warn("Sheet [{}] not found in the workbook. Skipping changes for this sheet.", sheetName);
            return;
        }

        List<Cell> cells = applyChangesToCells(changes, sheet);
        log.info("Applied change to sheet [{}], cells {}",
                sheetName, cells.stream().map(Cell::getStringCellValue).toList());
    }

    private List<Cell> applyChangesToCells(List<ChangedSubjectDto> changes, Sheet sheet) {
        return changes.stream()
                .map(changeCell(sheet))
                .toList();
    }

    private Function<ChangedSubjectDto, Cell> changeCell(Sheet sheet) {
        return change -> {
            MergedRegion mergedRegion = change.newChange().mergedRegion();
            if (isSingleCellMergedRegion(mergedRegion)) {
                log.warn("Incorrect region for the cell [{}]", change.newChange());
                change = ChangedSubjectDto.builder()
                        .newChange(
                                CellDto.builder()
                                        .data(change.newChange().data())
                                        .coordinate(change.newChange().coordinate())
                                        .mergedRegion(MergedRegion.EMPTY_REGION)
                                        .isMergedStart(false)
                                        .build()
                        )
                        .build();
            } else if (!isRegionInsertable(sheet, mergedRegion)) {
                log.warn("Region {} is overlapping with another one. Skipping changes for this sheet.", mergedRegion);
                return getCell(sheet, change.newChange().coordinate());
            }

            if (mergedRegion.isEmptyRegion()) {
                return changeSingleCell(sheet, change);
            }
            return changeRegionCell(sheet, change);
        };
    }

    private Cell changeSingleCell(Sheet sheet, ChangedSubjectDto change) {
        CoordinatesDto coordinates = change.newChange().coordinate();
        Cell cell = getCell(sheet, coordinates);
        cell.setCellValue(change.newChange().data());
        return cell;
    }

    private Cell getCell(Sheet sheet, CoordinatesDto coordinates) {
        Row row = sheet.getRow(coordinates.row());
        Cell cell = row.getCell(coordinates.col(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell;
    }

    private Cell changeRegionCell(Sheet sheet, ChangedSubjectDto change) {
        MergedRegion mergedRegion = change.newChange().mergedRegion();

        CellRangeAddress targetRegion = new CellRangeAddress(
                mergedRegion.firstRow(), mergedRegion.lastRow(),
                mergedRegion.firstColumn(), mergedRegion.lastColumn()
        );

        boolean regionExists = isRegionExists(sheet, targetRegion);

        if (!regionExists) {
            log.debug("Created region {}", targetRegion);
            sheet.addMergedRegion(targetRegion);
        }
        var changedCell = changeSingleCell(sheet, change);
        unmergeCellIfEmpty(sheet, change, targetRegion);

        return changedCell;
    }

    private boolean isRegionExists(Sheet sheet, CellRangeAddress targetRegion) {
        return sheet.getMergedRegions()
                .stream()
                .anyMatch(existingRegion -> existingRegion.equals(targetRegion));
    }

    private void unmergeCellIfEmpty(Sheet sheet, ChangedSubjectDto change, CellRangeAddress targetRegion) {
        MergedRegion mergedRegion = change.newChange().mergedRegion();

        Cell firstCell = sheet.getRow(mergedRegion.firstRow())
                .getCell(mergedRegion.firstColumn(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        if (firstCell.getStringCellValue().isBlank()) {
            sheet.removeMergedRegion(sheet.getMergedRegions().indexOf(targetRegion));
        }
    }
}

package org.denys.hudymov.schedule.editor.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.apache.poi.EmptyFileException;
import org.denys.hudymov.schedule.editor.domain.CellDto;
import org.denys.hudymov.schedule.editor.domain.CoordinatesDto;
import org.denys.hudymov.schedule.editor.domain.MergedRegion;
import org.denys.hudymov.schedule.editor.domain.RowDto;
import org.denys.hudymov.schedule.editor.domain.SheetDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class ExcelIOServiceImplTest {
    private static final String TEST_FOLDER_PATH = "C:/Users/Denys/My learning/4_Course/Diploma_Work/schedule-editor/src/test/schedule.test/";
    private static final boolean IS_TOP_LEFT_CELL = true;
    public static final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    @InjectMocks
    ExcelIOServiceImpl service;

    @Test
    @DisplayName("Test for readExcelFile method with an empty Excel file will throw EmptyFileException from poi library")
    void testReadExcelFileWithEmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("empty.xlsx", "empty.xlsx", XLSX_MIME_TYPE, new byte[0]);
        assertThrows(EmptyFileException.class, () -> service.readExcelFile(emptyFile));
    }

    @Test
    @DisplayName("Test for readExcelFile method with an invalid file type")
    void testReadExcelFileWithInvalidFile() {
        MultipartFile invalidFile = new MockMultipartFile("invalid.txt", "invalid.txt", "text/plain", "Invalid content".getBytes());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.readExcelFile(invalidFile));
        assertThat(exception.getMessage()).isEqualTo("Invalid file format, only .xls and .xlsx supported");
    }

    @Test
    @DisplayName("Test for readExcelFile method with multiple sheets")
    void testReadExcelFileWithMultipleSheets() throws IOException {
        var testFile = "sample_with_multiple_sheets.xls";
        var file = new File(TEST_FOLDER_PATH + testFile);
        InputStream inputFile = new FileInputStream(file);
        MultipartFile testExcelFile = new MockMultipartFile(
                testFile,
                testFile,
                XLSX_MIME_TYPE,
                inputFile
        );
        var expected = new HashMap<String, SheetDto>();

        var sheetDto1 = prepareSheetDto();
        var sheetDto2 = prepareSheetDto();

        expected.put("Sheet1", sheetDto1);
        expected.put("Sheet2", sheetDto2);


        var actual = service.readExcelFile(testExcelFile);


        assertThat(actual).containsAllEntriesOf(expected);
    }


    @Test
    @DisplayName("Test for readExcelFile method when excel file has merged cells")
    void testReadExcelFileWithMergedCells() throws IOException {

        var testFile = "sample_with_merge.xlsx";
        var file = new File(TEST_FOLDER_PATH + testFile);
        InputStream inputFile = new FileInputStream(file);
        MultipartFile testExcelFile = new MockMultipartFile(
                testFile,
                testFile,
                XLSX_MIME_TYPE,
                inputFile
        );

        var mergedCells1 = generateMergedCells("Групова динаміка і комунікації (лк.) доц.Сидорова М.Г.", GenerateMergedRegion(0, 0, 8, 0), generateCoordinatesDto(0, 0));
        var mergedCells2 = generateMergedCells("Технології Deep Learning (лк) доц.Сидорова М.Г.->>", GenerateMergedRegion(0, 1, 3, 2), generateCoordinatesDto(1, 0));
        var mergedCellsEmpty2 = generateMergedCells("", GenerateMergedRegion(0, 1, 3, 2), generateCoordinatesDto(2, 0));
        var mergedCells3 = generateMergedCells("Груп.динам.і комун. (лб.) доц.Сидорова М.Г.", GenerateMergedRegion(-1, -1, -1, -1), generateCoordinatesDto(3, 0));
        var mergedCells4 = generateMergedCells("Понеділок", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(4, 0));
        var mergedCells4Empty1 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(5, 0));
        var mergedCells4Empty2 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(6, 0));
        var mergedCells4Empty3 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(7, 0));
        var mergedCells4Empty4 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(8, 0));
        var mergedCells4Empty5 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(9, 0));
        var mergedCells4Empty6 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(10, 0));
        var mergedCells4Empty7 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(11, 0));
        var mergedCells4Empty8 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(12, 0));
        var mergedCells4Empty9 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13), generateCoordinatesDto(13, 0));

        var firstRow = generateRow(mergedCells1);
        var secondRow = generateRow(mergedCells2);
        var secondEmptyRow = generateRow(mergedCellsEmpty2);
        var thirdRow = generateRow(mergedCells3);
        var fourthRow = generateRow(mergedCells4);
        var fourthRowEmpty1 = generateRow(mergedCells4Empty1);
        var fourthRowEmpty2 = generateRow(mergedCells4Empty2);
        var fourthRowEmpty3 = generateRow(mergedCells4Empty3);
        var fourthRowEmpty4 = generateRow(mergedCells4Empty4);
        var fourthRowEmpty5 = generateRow(mergedCells4Empty5);
        var fourthRowEmpty6 = generateRow(mergedCells4Empty6);
        var fourthRowEmpty7 = generateRow(mergedCells4Empty7);
        var fourthRowEmpty8 = generateRow(mergedCells4Empty8);
        var fourthRowEmpty9 = generateRow(mergedCells4Empty9);


        var expected = new HashMap<String, SheetDto>();
        var rows = prepareSheetDto(firstRow, secondRow, secondEmptyRow, thirdRow, fourthRow, fourthRowEmpty1, fourthRowEmpty2, fourthRowEmpty3, fourthRowEmpty4, fourthRowEmpty5, fourthRowEmpty6, fourthRowEmpty7, fourthRowEmpty8, fourthRowEmpty9);
        expected.put("Sheet1", rows);

        var actual = service.readExcelFile(testExcelFile);
        System.out.println("actual: " + actual);

        assertThat(actual).isEqualTo(expected);
    }

    private static CoordinatesDto generateCoordinatesDto(int row, int col) {
        return CoordinatesDto.builder()
                .row(row)
                .col(col)
                .build();
    }

    private static SheetDto prepareSheetDto(RowDto... rowDtos) {
        return SheetDto.builder()
                .rowData(List.of(rowDtos))
                .build();
    }

    private RowDto generateRow(List<CellDto> mergedCells) {
        return RowDto.builder()
                .cells(mergedCells)
                .build();
    }

    private MergedRegion GenerateMergedRegion(int firstC, int firstR, int lastC, int lastR) {
        return MergedRegion.builder()
                .firstColumn(firstC)
                .firstRow(firstR)
                .lastColumn(lastC)
                .lastRow(lastR)
                .build();
    }

    private CellDto generateCellDto(String value, MergedRegion mergedRegion, boolean isTopLeftCell, CoordinatesDto coordinates) {
        return CellDto.builder()
                .data(value)
                .isMergedStart(isTopLeftCell)
                .mergedRegion(mergedRegion)
                .coordinate(coordinates)
                .build();
    }

    public List<CellDto> generateMergedCells(String value, MergedRegion mergedRegion, CoordinatesDto coordinates) {
        var emptyValue = "";
        int firstColumn = mergedRegion.firstColumn();
        int lastColumn = mergedRegion.lastColumn();
        int columnCount = lastColumn - firstColumn + 1;

        List<CellDto> cells = new ArrayList<>();

        for (int i = 0; i < columnCount; i++) {
            if (mergedRegion.isEmptyRegion()) {
                cells.add(generateCellDto(value, mergedRegion, !IS_TOP_LEFT_CELL, coordinates));
            } else if (i == 0 && !Objects.equals(value, emptyValue)) {
                cells.add(generateCellDto(value, mergedRegion, IS_TOP_LEFT_CELL, coordinates));
            } else {
                cells.add(generateCellDto(emptyValue, mergedRegion, !IS_TOP_LEFT_CELL, coordinates));
            }
            coordinates = CoordinatesDto.builder()
                    .row(coordinates.row())
                    .col(coordinates.col() + 1)
                    .build();
        }

        return cells;
    }
}
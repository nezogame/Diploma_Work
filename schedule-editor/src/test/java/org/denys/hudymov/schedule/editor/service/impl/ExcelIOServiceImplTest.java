package org.denys.hudymov.schedule.editor.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.denys.hudymov.schedule.editor.domain.CellDto;
import org.denys.hudymov.schedule.editor.domain.MergedRegion;
import org.denys.hudymov.schedule.editor.domain.RowDto;
import org.denys.hudymov.schedule.editor.domain.SheetDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ExcelIOServiceImplTest {
    private static final String TEST_FOLDER_PATH = "C:/Users/Denys/My learning/4_Course/Diploma_Work/schedule-editor/src/test/schedule.test/";
    private static final boolean IS_TOP_LEFT_CELL = true;
    @Mock
    ExcelIOServiceImpl service;


    @Test
    @DisplayName("Test for readExcelFile method when excel file has merged cells")
    void testReadExcelFileWithMergedCells() throws IOException {

        var testFile = "sample_with_merge.xlsx";
        var file = new File(TEST_FOLDER_PATH + testFile);
        InputStream inputFile = new FileInputStream(file);
        MultipartFile testExcelFile = new MockMultipartFile(testFile, inputFile);

        var mergedCells1 = generateMergedCells("Групова динаміка і комунікації (лк.) доц.Сидорова М.Г.", GenerateMergedRegion(0, 0, 8, 0));
        var mergedCells2 = generateMergedCells("Технології Deep Learning (лк) доц.Сидорова М.Г.->>", GenerateMergedRegion(0, 1, 3, 2));
        var mergedCellsEmpty2 = generateMergedCells("", GenerateMergedRegion(0, 1, 3, 2));
        var mergedCells3 = generateMergedCells("Груп.динам.і комун. (лб.) доц.Сидорова М.Г.", GenerateMergedRegion(-1, -1, -1, -1));
        var mergedCells4 = generateMergedCells("Понеділок", GenerateMergedRegion(0, 4, 0, 13));
        var mergedCells4Empty1 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13));
        var mergedCells4Empty2 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13));
        var mergedCells4Empty3 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13));
        var mergedCells4Empty4 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13));
        var mergedCells4Empty5 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13));
        var mergedCells4Empty6 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13));
        var mergedCells4Empty7 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13));
        var mergedCells4Empty8 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13));
        var mergedCells4Empty9 = generateMergedCells("", GenerateMergedRegion(0, 4, 0, 13));

        var firstRow = generateRow(mergedCells1, 0);
        var secondRow = generateRow(mergedCells2, 1);
        var secondEmptyRow = generateRow(mergedCellsEmpty2, 2);
        var thirdRow = generateRow(mergedCells3, 3);
        var fourthRow = generateRow(mergedCells4, 4);
        var fourthRowEmpty1 = generateRow(mergedCells4Empty1, 5);
        var fourthRowEmpty2 = generateRow(mergedCells4Empty2, 6);
        var fourthRowEmpty3 = generateRow(mergedCells4Empty3, 7);
        var fourthRowEmpty4 = generateRow(mergedCells4Empty4, 8);
        var fourthRowEmpty5 = generateRow(mergedCells4Empty5, 9);
        var fourthRowEmpty6 = generateRow(mergedCells4Empty6, 10);
        var fourthRowEmpty7 = generateRow(mergedCells4Empty7, 11);
        var fourthRowEmpty8 = generateRow(mergedCells4Empty8, 12);
        var fourthRowEmpty9 = generateRow(mergedCells4Empty9, 13);


        var expected = new HashMap<String, SheetDto>();
        var rows = SheetDto.builder()
                .rowData(
                        List.of(firstRow, secondRow, secondEmptyRow, thirdRow, fourthRow, fourthRowEmpty1, fourthRowEmpty2
                                , fourthRowEmpty3, fourthRowEmpty4, fourthRowEmpty5, fourthRowEmpty6, fourthRowEmpty7, fourthRowEmpty8, fourthRowEmpty9)
                )
                .build();
//        expected.put("Sheet1", rows);

        var actual = service.readExcelFile(testExcelFile);
        System.out.println("actual: " + actual);

        assertThat(actual).isEqualTo(expected);
        verify(service).readExcelFile(testExcelFile);

    }

    private RowDto generateRow(List<CellDto> mergedCells, int rowNumber) {
        return RowDto.builder()
                .rowNumber(rowNumber)
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

    private CellDto generateCellDto(String value, MergedRegion mergedRegion, boolean isTopLeftCell) {
        return CellDto.builder()
                .data(value)
                .isMergedStart(isTopLeftCell)
                .mergedRegion(mergedRegion)
                .build();
    }

    public List<CellDto> generateMergedCells(String value, MergedRegion mergedRegion) {
        var emptyValue = "";
        int firstColumn = mergedRegion.firstColumn();
        int lastColumn = mergedRegion.lastColumn();
        int columnCount = lastColumn - firstColumn + 1;

        List<CellDto> cells = new ArrayList<>();

        for (int i = 0; i < columnCount; i++) {
            if (mergedRegion.equals(MergedRegion.EMPTY_REGION)) {
                cells.add(generateCellDto(value, mergedRegion, !IS_TOP_LEFT_CELL));
            } else if (i == 0 && !Objects.equals(value, emptyValue)) {
                cells.add(generateCellDto(value, mergedRegion, IS_TOP_LEFT_CELL));
            } else {
                cells.add(generateCellDto(emptyValue, mergedRegion, !IS_TOP_LEFT_CELL));
            }
        }

        return cells;
    }
}
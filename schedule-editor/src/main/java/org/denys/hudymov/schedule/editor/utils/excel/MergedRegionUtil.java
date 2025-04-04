package org.denys.hudymov.schedule.editor.utils.excel;

import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.denys.hudymov.schedule.editor.domain.CellDto;
import org.denys.hudymov.schedule.editor.domain.MergedRegion;

public class MergedRegionUtil {
    private static final boolean IS_MERGED_START = true;

    private MergedRegionUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static CellDto setCellInMergedRegion(Sheet sheet, Cell cell) {
        int row = cell.getRowIndex();
        int col = cell.getColumnIndex();

        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        for (CellRangeAddress mergedRegion : mergedRegions) {
            var firstRow = mergedRegion.getFirstRow();
            var firstColumn = mergedRegion.getFirstColumn();
            var isFirstCellOfTheRegion = row == firstRow && col == firstColumn;

            if (isFirstCellOfTheRegion) {
                return generateCellDto(cell, mergedRegion, IS_MERGED_START);
            } else if (mergedRegion.isInRange(row, col)) {
                return generateCellDto(cell, mergedRegion, !IS_MERGED_START);
            }
        }
        return generateCellDto(cell, !IS_MERGED_START);
    }

    private static CellDto generateCellDto(Cell cell, CellRangeAddress mergedRegion, boolean isMergedStart) {
        return CellDto.builder()
                .data(cell.toString())
                .isMergedStart(isMergedStart)
                .mergedRegion(MergedRegion.builder()
                        .firstColumn(mergedRegion.getFirstColumn())
                        .firstRow(mergedRegion.getFirstRow())
                        .lastColumn(mergedRegion.getLastColumn())
                        .lastRow(mergedRegion.getLastRow())
                        .build())
                .build();
    }

    private static CellDto generateCellDto(Cell cell, boolean isMergedStart) {
        return CellDto.builder()
                .data(cell.toString())
                .isMergedStart(isMergedStart)
                .mergedRegion(MergedRegion.EMPTY_REGION)
                .build();
    }
}

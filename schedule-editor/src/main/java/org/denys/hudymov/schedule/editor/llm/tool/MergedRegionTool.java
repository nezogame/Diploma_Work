package org.denys.hudymov.schedule.editor.llm.tool;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.denys.hudymov.schedule.editor.domain.MergedRegion;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MergedRegionTool {
    MergedRegionTool() {
        throw new IllegalStateException("Utility class");
    }

    @Tool(name = "is_single_cell_merged_region", value = "Validates whether the provided merged region " +
            "represents a single cell. In case if single region mergedRegion must be replaced with EMPTY_REGION")
    public static boolean isSingleCellMergedRegion(MergedRegion mergedRegion) {
        if (mergedRegion == null) {
            return true;
        }

        var numberOfCell = (mergedRegion.lastRow() - mergedRegion.firstRow() + 1) *
                (mergedRegion.lastColumn() - mergedRegion.firstColumn() + 1);
        return numberOfCell < 2;
    }

    @Tool(name = "is_region_insertable", value = "Validates whether it is possible to insert a " +
            "region without overlapping with existing merged regions.")
    public static boolean isRegionInsertable(Sheet sheet, MergedRegion mergedRegion) {
        CellRangeAddress newRegion = new CellRangeAddress(
                mergedRegion.firstRow(), mergedRegion.lastRow(),
                mergedRegion.firstColumn(), mergedRegion.lastColumn()
        );

        log.info("At sheet [{}] Validating region [{}]", sheet.getSheetName(), newRegion.formatAsString());
        return sheet.getMergedRegions()
                .stream()
                .noneMatch(existingRegion -> existingRegion.intersects(newRegion));
    }
}

package org.denys.hudymov.schedule.editor.domain;

import lombok.Builder;

@Builder
public record CellDto(
//        XSSFCellStyle cellStyle,
        String data,
        MergedRegion mergedRegion,
        Boolean isMergedStart

) {
}

package org.denys.hudymov.schedule.editor.domain;

import lombok.Builder;
import org.apache.poi.ss.usermodel.CellStyle;

@Builder
public record CellDto(
        CellStyle cellStyle,
        String data
) {
}

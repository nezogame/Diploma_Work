package org.denys.hudymov.schedule.editor.domain;

import java.util.List;
import lombok.Builder;

@Builder
public record RowDto(
        int rowNumber,
        List<CellDto> cells
) {
}

package org.denys.hudymov.schedule.editor.domain;

import java.util.List;
import lombok.Builder;

@Builder
public record SheetDto(
        List<RowDto> rowData
) {

}

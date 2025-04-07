package org.denys.hudymov.schedule.editor.domain;

import lombok.Builder;

@Builder
public record CellDto(
        String data,
        MergedRegion mergedRegion,
        CoordinatesDto coordinate,
        Boolean isMergedStart
) {
}

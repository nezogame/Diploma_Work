package org.denys.hudymov.schedule.editor.domain;

import lombok.Builder;

@Builder
public record CoordinatesDto(
        int row,
        int col
) {
}

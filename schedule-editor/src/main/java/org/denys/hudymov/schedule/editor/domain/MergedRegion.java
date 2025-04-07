package org.denys.hudymov.schedule.editor.domain;

import lombok.Builder;

@Builder
public record MergedRegion(
        int firstRow,
        int lastRow,
        int firstColumn,
        int lastColumn
) {
    private static final int EMPTY_REGION_NUMBER = -1;

    public static MergedRegion EMPTY_REGION = MergedRegion.builder()
            .lastRow(EMPTY_REGION_NUMBER)
            .lastColumn(EMPTY_REGION_NUMBER)
            .firstRow(EMPTY_REGION_NUMBER)
            .firstColumn(EMPTY_REGION_NUMBER)
            .build();

    public boolean isEmptyRegion() {
        return this.equals(EMPTY_REGION);
    }
}

package org.denys.hudymov.schedule.editor.domain;

import lombok.Builder;

@Builder
public record ChangedSubjectDto(
        String changesDescription,
        RowDto newChangeInRow,
        RowDto changeAppliedToOtherRow
) {
}

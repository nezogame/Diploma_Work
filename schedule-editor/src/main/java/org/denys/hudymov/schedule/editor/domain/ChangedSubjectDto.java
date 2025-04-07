package org.denys.hudymov.schedule.editor.domain;

import lombok.Builder;

@Builder
public record ChangedSubjectDto(
        String changeDescription,
        CellDto newChange
) {
}

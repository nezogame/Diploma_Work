package org.denys.hudymov.schedule.editor.domain;

import java.util.Map;
import lombok.Builder;

@Builder
public record ScheduleAnalyseResponse(
        String analyseResult,
        Map<String, ChangedSubjectDto> changedSubjects
) {

}

package org.denys.hudymov.schedule.editor.domain;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record ScheduleAnalyseDto(
        String analyseResult,
        Map<String, List<ChangedSubjectDto>> changedSubjects
) {

}

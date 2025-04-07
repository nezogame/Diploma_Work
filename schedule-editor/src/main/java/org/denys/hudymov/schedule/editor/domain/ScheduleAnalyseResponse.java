package org.denys.hudymov.schedule.editor.domain;

import java.util.Map;
import java.util.Optional;
import lombok.Builder;

@Builder
public record ScheduleAnalyseResponse(
        String analyseResult,
        Optional<String> changesDescription,
        Map<String, SheetDto> excelFile
) {

}

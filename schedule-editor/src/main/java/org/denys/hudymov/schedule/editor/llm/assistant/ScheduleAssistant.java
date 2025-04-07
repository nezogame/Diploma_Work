package org.denys.hudymov.schedule.editor.llm.assistant;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;
import org.denys.hudymov.schedule.editor.domain.ScheduleAnalyseResponse;
import org.denys.hudymov.schedule.editor.domain.SheetDto;

@AiService
public interface ScheduleAssistant {
    @SystemMessage("You are the schedule editor's assistant. " +
            "Help the user create a convenient schedule and resolve " +
            "any overlapping lecture subjects by reorganizing it as needed.")
    Result<ScheduleAnalyseResponse> generateSchedule(Workbook scheduleTemplate, Workbook educationalProgram);

    @SystemMessage("You are the schedule editor's assistant. " +
            "Help the user check the schedule for the convenience and resolve " +
            "any overlapping lecture subjects by reorganizing it as needed. " +
            "Provide the resulting schedule in a clear, tabular format, " +
            "suitable for direct copy-pasting into a spreadsheet." +
            "Please explicitly mention any changes made to resolve overlapping subjects.")
    Result<ScheduleAnalyseResponse> generateSchedule(Map<String, SheetDto> scheduleData);

    @SystemMessage("You are the assistant to the schedule editor. " +
            "If a user asks how to resolve overlapping conflicts, " +
            "suggest several ways to address the issue.")
    String chatAboutSchedule(String prompt);
}

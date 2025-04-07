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

    @SystemMessage(fromResource = "Instruction_for_LLM/Schedule_rules.txt")
    Result<ScheduleAnalyseResponse> generateSchedule(Map<String, SheetDto> scheduleData);

    @SystemMessage("You are the assistant to the schedule editor. " +
            "If a user asks how to resolve overlapping conflicts, " +
            "suggest several ways to address the issue.")
    String chatAboutSchedule(String prompt);
}

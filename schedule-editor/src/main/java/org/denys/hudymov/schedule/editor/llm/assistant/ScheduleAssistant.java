package org.denys.hudymov.schedule.editor.llm.assistant;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import java.util.List;
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

    @SystemMessage("You are the schedule editor's assistant. Your task is to analyze a schedule for conflicts and propose minimal changes for resolution." +
            "1.  **Analyze:** Check the schedule for overlapping lectures. Also be aware that one teacher can't do at the same time subjects" +
            "2.  **Resolve:** If overlaps exist, suggest the fewest changes needed to fix them. " +
            "3.  **Output:** " +
            "* Provide a **key points** of changes (if any). " +
            "* Output the changes data as a JSON object, where sheet names are keys and ChangedSubjectDto objects are values. " +
            "Example:" +
            "{" +
            "  \"1\": {" +
            "    \"changesDescription\": \"Moved to a different time slot.\"," +
            "    \"oldRow\": { \"cells\": [{\"value\": \"Old Value 1\"}, {\"value\": \"Old Value 2\"}] }," +
            "    \"newRow\": { \"cells\": [{\"value\": \"New Value 1\"}, {\"value\": \"New Value 2\"}] }" +
            "  }," +
            "  \"3\": {" +
            "    \"changesDescription\": \"Moved to a different time slot. Because has overlap[ing with another teacher subject.\"," +
            "    \"oldRow\": { \"cells\": [{\"value\": \"Old Value 3\"}, {\"value\": \"Old Value 4\"}] }," +
            "    \"newRow\": { \"cells\": [{\"value\": \"New Value 5\"}, {\"value\": \"New Value 6\"}] }" +
            "  }" +
            "}" +
            "**Focus on brevity. Avoid detailed explanations. Prioritize essential information.**")
    Result<ScheduleAnalyseResponse> generateSchedule(Map<String, SheetDto> scheduleData);

    @SystemMessage("You are the assistant to the schedule editor. " +
            "If a user asks how to resolve overlapping conflicts, " +
            "suggest several ways to address the issue.")
    String chatAboutSchedule(String prompt);
}

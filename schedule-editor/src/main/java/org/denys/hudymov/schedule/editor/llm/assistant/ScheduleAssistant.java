package org.denys.hudymov.schedule.editor.llm.assistant;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import java.util.Map;
import org.denys.hudymov.schedule.editor.domain.ScheduleAnalyseDto;
import org.denys.hudymov.schedule.editor.domain.SheetDto;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

//ollamaChatModel
@AiService(wiringMode = EXPLICIT, chatModel = "googleAiGeminiChatModel", tools = {"mergedRegionTool", "scheduleValidator"})
public interface ScheduleAssistant {
    @SystemMessage(fromResource = "Instruction_for_LLM/Schedule_compose_rules.txt")
    @UserMessage("There is my schedule {{scheduleTemplate}} and educational program {{educationPlan}}, " +
            "consider both and populate schedule with all data from educational program")
    Result<ScheduleAnalyseDto> generateSchedule(@V("scheduleTemplate") Map<String, SheetDto> scheduleTemplate,
                                                @V("educationPlan") Map<String, SheetDto> educationalProgram);

    @SystemMessage(fromResource = "Instruction_for_LLM/Schedule_rules.txt")
    @UserMessage("There is my schedule {{scheduleTemplate}} for the review")
    Result<ScheduleAnalyseDto> generateSchedule(@V("scheduleTemplate") Map<String, SheetDto> scheduleData);

/*    @SystemMessage("You are the assistant to the schedule editor. " +
            "If a user asks how to resolve overlapping conflicts, " +
            "suggest several ways to address the issue.")
    String chatAboutSchedule(String prompt);*/
}

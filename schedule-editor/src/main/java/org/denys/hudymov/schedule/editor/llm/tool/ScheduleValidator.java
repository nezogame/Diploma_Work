package org.denys.hudymov.schedule.editor.llm.tool;

import dev.langchain4j.agent.tool.Tool;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleValidator {

    public static final String CHOSEN_SUBJECT = "ВД";

    @Tool
    public boolean isInsertionAllowedInRange(Sheet sheet, String groupNameWithCode, int columnIndex) {
        CellRangeAddress range = findMergedRangeByGroupName(sheet, groupNameWithCode);

        if (range == null) {
            log.warn("No merged range found for group [{}].", groupNameWithCode);
            return false;
        }

        boolean isInMergedRange = range.getFirstColumn() <= columnIndex && columnIndex <= range.getLastColumn();
        log.info("Column [{}] {} within the range of group [{}].", columnIndex,
                isInMergedRange ? "is" : "is not", groupNameWithCode);
        return isInMergedRange;
    }

    private CellRangeAddress findMergedRangeByGroupName(Sheet sheet, String groupNameWithCode) {
        for (CellRangeAddress range : sheet.getMergedRegions()) {
            Row row = sheet.getRow(range.getFirstRow());
            if (row != null) {
                Cell cell = row.getCell(range.getFirstColumn());

                if (isMatchingGroup(groupNameWithCode, cell)) {
                    log.info("Found merged range [{}] for group [{}].", range.formatAsString(), groupNameWithCode);
                    return range;
                }
            }
        }
        log.warn("Merged range for group [{}] not found.", groupNameWithCode);
        return null;
    }

    private boolean isMatchingGroup(String groupNameWithCode, Cell cell) {
        var cellNotEmpty = cell.getCellType() == CellType.STRING && cell.getStringCellValue() != null;
        var correctGroup = cell.getStringCellValue().trim().equalsIgnoreCase(groupNameWithCode);
        return cellNotEmpty && correctGroup;
    }

    @Tool
    public double calculateSubjectHours(Sheet sheet, String subjectName) {
        double totalHours = 0.0;

        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = cell.getStringCellValue();

                var find = isSubjectPresent(subjectName, cellValue);
                if (find) {
                    boolean isMerged = isMergedCell(sheet, cell);
                    totalHours += isMerged ? 1.0 : 0.5;
                }
            }
        }

        log.info("Total hours for subject [{}]: {}", subjectName, totalHours);
        return totalHours;
    }

    private boolean isSubjectPresent(String subjectName, String cellValue) {
        if (cellValue == null) {
            return false;
        }
        boolean searchedSubject = cellValue.toLowerCase().contains(subjectName.toLowerCase());
        boolean chosenSubject = cellValue.contains(CHOSEN_SUBJECT);
        return searchedSubject && !chosenSubject;
    }

    private boolean isMergedCell(Sheet sheet, Cell cell) {
        for (CellRangeAddress range : sheet.getMergedRegions()) {
            if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return true;
            }
        }
        return false;
    }


    @Tool
    public boolean isCellInsertionAllowed(Sheet sheet, String groupNameWithCode, int colIndex) {
        int groupColumn = findColumnGroupByName(sheet, groupNameWithCode);
        if (groupColumn == -1) {
            return false;
        }

        if (groupColumn == colIndex) {
            log.info("Column [{}] belongs to group [{}]. Insertion is allowed.", colIndex, groupNameWithCode);
            return true;
        } else {
            log.warn("Column [{}] does not belong to group [{}]. Insertion is not allowed.", colIndex, groupNameWithCode);
            return false;
        }
    }

    @Tool
    public boolean isColumnsBelongToGroups(Sheet sheet, List<String> groupNamesWithCode, List<Integer> colIndexes) {
        if (groupNamesWithCode.size() != colIndexes.size()) {
            log.warn("Mismatch between number of groups and provided column indexes");
            return false;
        }

        for (int i = 0; i < groupNamesWithCode.size(); i++) {
            int groupColumn = findColumnGroupByName(sheet, groupNamesWithCode.get(i));
            if (groupColumn != colIndexes.get(i)) {
                log.warn("Group [{}] does not match provided column index [{}]", groupNamesWithCode.get(i), colIndexes.get(i));
                return false;
            }
        }

        log.info("All provided groups correspond to their respective column indexes.");
        return true;
    }

    @Tool
    public int findColumnGroupByName(Sheet sheet, String groupNameWithCode) {
        for (Row row : sheet) {
            for (Cell cell : row) {

                if (isMatchingGroup(groupNameWithCode, cell)) {
                    log.info("Group [{}] found in column [{}]", groupNameWithCode, cell.getColumnIndex());
                    return cell.getColumnIndex();
                }
            }
        }
        log.warn("Group [{}] not found in the sheet", groupNameWithCode);
        return -1;
    }


    @Tool
    public String findGroupNameWithGroupCode(String groupName, int yearOfLearning, int numberOfRepeat) {
        var currentYear = java.time.LocalDate.now();
        int adjustedYearOfLearning = (currentYear.getMonthValue() <= 6) ? yearOfLearning - 1 : yearOfLearning;

        int roundedRepeatNumber = Math.round((float) (numberOfRepeat + 1) / 2);

        String groupCode = groupName + "-" + adjustedYearOfLearning + "-" + roundedRepeatNumber;
        log.info("Group code: [{}]", groupCode);
        return groupCode;
    }
}

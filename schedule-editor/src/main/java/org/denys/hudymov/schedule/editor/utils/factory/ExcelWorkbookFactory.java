package org.denys.hudymov.schedule.editor.utils.factory;

import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
public class ExcelWorkbookFactory {

    private ExcelWorkbookFactory() {
        throw new IllegalStateException("Factory class");
    }

    public static Workbook createWorkbook(String fileName, InputStream is) throws IOException {
        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(is);
        } else {
            log.debug("File name: {}", fileName);
            throw new IllegalArgumentException("Invalid file format, only .xls and .xlsx supported");
        }
    }
}

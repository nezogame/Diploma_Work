package org.denys.hudymov.schedule.editor.factory;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWorkbookFactory {

    public static Workbook createWorkbook(String fileName, InputStream is) throws IOException {
        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(is);
        } else {
            throw new IllegalArgumentException("Invalid file format, only .xls and .xlsx supported");
        }
    }
}

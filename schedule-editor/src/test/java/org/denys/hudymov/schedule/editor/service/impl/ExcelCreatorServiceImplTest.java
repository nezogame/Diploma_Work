package org.denys.hudymov.schedule.editor.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExcelCreatorServiceImplTest {

    private final ExcelCreatorServiceImpl excelCreatorService = new ExcelCreatorServiceImpl();
    private Workbook workbook;

    @BeforeEach
    void createWorkbook() {
        workbook = new XSSFWorkbook();
    }

    @Test
    void createChangedExcelFile_validInput_createsAndReturnsFileSystemResource() throws IOException {
        String mockFilename = "test.xlsx";

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(mockFilename);

        FileSystemResource result = excelCreatorService.createChangedExcelFile(workbook, mockFile);

        assertNotNull(result);
        assertNotNull(result.getFile());
        assertEquals("modified-schedule", result.getFile().getName().substring(0, 17));
        verify(mockFile, times(1)).getOriginalFilename();
    }

    @Test
    void createChangedExcelFile_nullWorkbook_throwsException() {
        MultipartFile mockFile = mock(MultipartFile.class);

        assertThrows(NullPointerException.class, () -> excelCreatorService.createChangedExcelFile(null, mockFile));
    }

    @Test
    void createChangedExcelFile_nullFile_throwsException() {
        assertThrows(NullPointerException.class, () -> excelCreatorService.createChangedExcelFile(workbook, null));
    }

    @Test
    void createChangedExcelFile_fileWithoutExtension_throwsException() {
        String expectedErrorMessage = "File name is empty, or has no extension";
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("testfile");

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> excelCreatorService.createChangedExcelFile(workbook, mockFile));
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }

    @Test
    void createChangedExcelFile_emptyFileName_throwsException() {
        String expectedErrorMessage = "File name is empty, or has no extension";
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("");

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> excelCreatorService.createChangedExcelFile(workbook, mockFile));
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }

    @Test
    void createChangedExcelFile_successfullyWritesDataToFile() throws IOException {
        workbook.createSheet("TestSheet");
        String filename = "schedule.xlsx";

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(filename);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        FileSystemResource result = excelCreatorService.createChangedExcelFile(workbook, mockFile);

        assertNotNull(result);
        assertNotNull(result.getFile());
        verify(mockFile, times(1)).getOriginalFilename();
    }
}
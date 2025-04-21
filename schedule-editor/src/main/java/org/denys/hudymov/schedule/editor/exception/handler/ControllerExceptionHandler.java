package org.denys.hudymov.schedule.editor.exception.handler;

import java.io.IOException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {
    private static final String MALFORMED_JSON_FORMAT = "Malformed JSON request";
    public static final String HTTP_ERROR_500 = "HTTP error (500)";
    public static final String FAILED_TO_APPLY_CHANGES_TO_THE_EXCEL_FILE = "Failed to apply changes to the Excel file.";

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Error> handleIOException(IOException ex) {
        log.error(FAILED_TO_APPLY_CHANGES_TO_THE_EXCEL_FILE, ex);
        var message = wrapMessageToResponse(ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Error> handleRuntimeException(RuntimeException ex) {
        boolean modelServerError = ex.getMessage() != null && ex.getMessage().contains(HTTP_ERROR_500);
        if (!modelServerError) {
            throw ex;
        }

        var message = wrapMessageToResponse(ex.getMessage(),ex);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Error> handleNoSuchElementException(NoSuchElementException ex) {
        var message = wrapMessageToResponse(ex.getMessage(),ex);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException ex) {
        var message = wrapMessageToResponse(ex.getMessage(), ex);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleHttpMessageNotReadableException() {
        var message = wrapMessageToResponse(MALFORMED_JSON_FORMAT);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    private Error wrapMessageToResponse(String exMessage, Throwable ex) {
        log.error(exMessage,ex);
        return new Error(exMessage);
    }

    private Error wrapMessageToResponse(String exMessage) {
        log.error(exMessage);
        return new Error(exMessage);
    }
}

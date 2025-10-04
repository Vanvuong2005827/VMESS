package com.vuong.vmess.exception;

import com.vuong.vmess.base.RestData;
import com.vuong.vmess.base.VsResponseUtil;
import com.vuong.vmess.exception.extended.InvalidException;
import com.vuong.vmess.exception.extended.NotFoundException;
import com.vuong.vmess.exception.extended.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestData<?>> handlerNotFoundException(NotFoundException ex) {
        log.error(ex.getMessage());
        return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<RestData<?>> handlerInvalidException(InvalidException ex) {
        log.error(ex.getMessage(), ex);
        return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<RestData<?>> handleUnauthorizedException(UnauthorizedException ex) {
        log.error(ex.getMessage(), ex);
        return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
    }

    // Bạn có thể thêm handler cho Exception chung
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestData<?>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return VsResponseUtil.error(HttpStatus.valueOf(500), "Internal server error");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestData<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        var fe = ex.getBindingResult().getFieldError();
        assert fe != null;
        String msg = fe.getDefaultMessage();
        String error = fe.getField() + ": " + msg; // example: "password: This field can't be blank"

        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<RestData<?>> handleSQLException(SQLException ex) {
        log.error(ex.getMessage(), ex);
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}

package com.share.lifetime.common.controller;

import java.lang.reflect.Type;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.event.Level;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.share.lifetime.common.exception.AbstractHttpException;
import com.share.lifetime.common.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    private Gson gson = new Gson();
    private static Type mapType = new TypeToken<Map<String, Object>>() {

        private static final long serialVersionUID = 1L;
    }.getType();

    // 处理系统内置的Exception
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> exception(HttpServletRequest request, Throwable ex) {
        return handleError(request, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(final Throwable throwable, final Model model) {
        log.error("Exception during execution of application", throwable);
        String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeException.class})
    public ResponseEntity<Map<String, Object>> badRequest(HttpServletRequest request, ServletException ex) {
        return handleError(request, HttpStatus.BAD_REQUEST, ex, Level.WARN);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Map<String, Object>> restTemplateException(HttpServletRequest request,
        HttpStatusCodeException ex) {
        return handleError(request, ex.getStatusCode(), ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> accessDeny(HttpServletRequest request, AccessDeniedException ex) {
        return handleError(request, HttpStatus.FORBIDDEN, ex);
    }

    // 处理自定义Exception
    @ExceptionHandler({AbstractHttpException.class})
    public ResponseEntity<Map<String, Object>> badRequest(HttpServletRequest request, AbstractHttpException ex) {
        return handleError(request, ex.getHttpStatus(), ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(HttpServletRequest request,
        MethodArgumentNotValidException ex) {
        final Optional<ObjectError> firstError = ex.getBindingResult().getAllErrors().stream().findFirst();
        if (firstError.isPresent()) {
            final String firstErrorMessage = firstError.get().getDefaultMessage();
            return handleError(request, HttpStatus.BAD_REQUEST, new BadRequestException(firstErrorMessage));
        }
        return handleError(request, HttpStatus.BAD_REQUEST, ex);
    }

    private ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request, HttpStatus status,
        Throwable ex) {
        return handleError(request, status, ex, Level.ERROR);
    }

    private ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request, HttpStatus status, Throwable ex,
        Level logLevel) {
        String message = ex.getMessage();
        printLog(message, ex, logLevel);

        Map<String, Object> errorAttributes = new HashMap<>();
        boolean errorHandled = false;

        if (ex instanceof HttpStatusCodeException) {
            try {
                errorAttributes = gson.fromJson(((HttpStatusCodeException)ex).getResponseBodyAsString(), mapType);
                status = ((HttpStatusCodeException)ex).getStatusCode();
                errorHandled = true;
            } catch (Throwable th) {
                // ignore
            }
        }

        if (!errorHandled) {
            errorAttributes.put("status", status.value());
            errorAttributes.put("message", message);
            errorAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            errorAttributes.put("exception", ex.getClass().getName());

        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorAttributes, headers, status);
    }

    // 打印日志, 其中logLevel为日志级别: ERROR/WARN/DEBUG/INFO/TRACE
    private void printLog(String message, Throwable ex, Level logLevel) {
        switch (logLevel) {
            case ERROR:
                log.error(message, ex);
                break;
            case WARN:
                log.warn(message, ex);
                break;
            case DEBUG:
                log.debug(message, ex);
                break;
            case INFO:
                log.info(message, ex);
                break;
            case TRACE:
                log.trace(message, ex);
                break;
        }

        // Tracer.logError(ex);
    }

}

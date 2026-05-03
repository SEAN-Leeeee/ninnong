package com.sean.ninnong.common;

import com.sean.ninnong.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return response(HttpStatus.BAD_REQUEST, message);
    }

    // 인증/인가
    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponse> handleLoginFailed(LoginFailedException e) {
        return response(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        return response(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedTeamAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedTeamAccess(UnauthorizedTeamAccessException e) {
        return response(HttpStatus.FORBIDDEN, e.getMessage());
    }

    // 리소스 없음
    @ExceptionHandler({
        TeamNotFoundException.class,
        TeamMemberNotFoundException.class,
        ApplicationNotFoundException.class,
        PostNotFoundException.class,
        UserEmailNotFoundException.class,
        RefreshTokenNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e) {
        return response(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 중복/충돌
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {
        return response(HttpStatus.CONFLICT, e.getMessage());
    }

    // 토큰 관련
    @ExceptionHandler({InvalidRefreshTokenException.class, RefreshTokenMismatchException.class})
    public ResponseEntity<ErrorResponse> handleTokenError(RuntimeException e) {
        return response(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // 그 외 — 내부 메시지 노출 차단, 서버 로그에만 기록
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .status(status.value())
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}

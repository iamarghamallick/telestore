package com.argha.telestore.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.argha.telestore.dto.ApiResponse;

import io.jsonwebtoken.ExpiredJwtException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse> handleValidationErrors(
                        MethodArgumentNotValidException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse> handleConstraintViolation(
                        MethodArgumentTypeMismatchException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponse> handleMissingServletRequestParameter(
                        MissingServletRequestParameterException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(MissingPathVariableException.class)
        public ResponseEntity<ApiResponse> handleMissingPathVariable(
                        MissingPathVariableException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiResponse> handleHttpRequestMethodNotSupported(
                        HttpRequestMethodNotSupportedException ex) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                                new ApiResponse(HttpStatus.METHOD_NOT_ALLOWED.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
        public ResponseEntity<ApiResponse> handleHttpMediaTypeNotSupported(
                        HttpMediaTypeNotSupportedException ex) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                                new ApiResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ApiResponse> handleNoHandlerFound(
                        NoHandlerFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                new ApiResponse(HttpStatus.NOT_FOUND.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                new ApiResponse(HttpStatus.CONFLICT.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(InvalidAccessTokenException.class)
        public ResponseEntity<ApiResponse> handleInvalidAccessToken(InvalidAccessTokenException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                new ApiResponse(HttpStatus.UNAUTHORIZED.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                new ApiResponse(HttpStatus.UNAUTHORIZED.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ApiResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ApiResponse> handleUserNotFound(UserNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                new ApiResponse(HttpStatus.NOT_FOUND.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
                return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(
                                new ApiResponse(HttpStatus.CONTENT_TOO_LARGE.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(ExpiredJwtException.class)
        public ResponseEntity<ApiResponse> handleExpiredJwt(ExpiredJwtException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                new ApiResponse(HttpStatus.UNAUTHORIZED.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(InvalidPasswordResetTokenException.class)
        public ResponseEntity<ApiResponse> handleInvalidPasswordResetToken(InvalidPasswordResetTokenException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(InvalidEmailVerificationTokenException.class)
        public ResponseEntity<ApiResponse> handleInvalidEmailVerificationToken(
                        InvalidEmailVerificationTokenException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(EmailNotVerifiedException.class)
        public ResponseEntity<ApiResponse> handleEmailNotVerified(
                        EmailNotVerifiedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                                new ApiResponse(HttpStatus.FORBIDDEN.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                ex.getMessage(), LocalDateTime.now()));
        }
}
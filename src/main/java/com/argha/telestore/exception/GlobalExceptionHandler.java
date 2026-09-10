package com.argha.telestore.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
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
import com.argha.telestore.dto.ApiResponseCode;

import io.jsonwebtoken.ExpiredJwtException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse> handleConstraintViolation(
                        MethodArgumentTypeMismatchException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ApiResponseCode.INVALID_REQUEST,
                                                ex.getMessage()));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ApiResponseCode.INVALID_REQUEST,
                                                ex.getMessage()));
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponse> handleMissingServletRequestParameter(
                        MissingServletRequestParameterException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ApiResponseCode.REQUIRED_FIELD_MISSING,
                                                ex.getMessage()));
        }

        @ExceptionHandler(MissingPathVariableException.class)
        public ResponseEntity<ApiResponse> handleMissingPathVariable(
                        MissingPathVariableException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ApiResponseCode.REQUIRED_FIELD_MISSING,
                                                ex.getMessage()));
        }

        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiResponse> handleHttpRequestMethodNotSupported(
                        HttpRequestMethodNotSupportedException ex) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                                new ApiResponse(HttpStatus.METHOD_NOT_ALLOWED.value(),
                                                ApiResponseCode.METHOD_NOT_ALLOWED,
                                                ex.getMessage()));
        }

        @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
        public ResponseEntity<ApiResponse> handleHttpMediaTypeNotSupported(
                        HttpMediaTypeNotSupportedException ex) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                                new ApiResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                                                ApiResponseCode.UNSUPPORTED_MEDIA_TYPE,
                                                ex.getMessage()));
        }

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ApiResponse> handleNoHandlerFound(
                        NoHandlerFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                new ApiResponse(HttpStatus.NOT_FOUND.value(),
                                                ApiResponseCode.RESOURCE_NOT_FOUND,
                                                ex.getMessage()));
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                new ApiResponse(HttpStatus.CONFLICT.value(),
                                                ApiResponseCode.DATA_CONFLICT,
                                                ex.getMessage()));
        }

        @ExceptionHandler(InvalidAccessTokenException.class)
        public ResponseEntity<ApiResponse> handleInvalidAccessToken(InvalidAccessTokenException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                new ApiResponse(HttpStatus.UNAUTHORIZED.value(),
                                                ApiResponseCode.TOKEN_INVALID,
                                                ex.getMessage()));
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                new ApiResponse(HttpStatus.UNAUTHORIZED.value(),
                                                ApiResponseCode.INVALID_CREDENTIALS,
                                                ex.getMessage()));
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ApiResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ApiResponseCode.USER_ALREADY_EXISTS,
                                                ex.getMessage()));
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ApiResponse> handleUserNotFound(UserNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                new ApiResponse(HttpStatus.NOT_FOUND.value(),
                                                ApiResponseCode.USER_NOT_FOUND,
                                                ex.getMessage()));
        }

        @ExceptionHandler(FolderNotFoundException.class)
        public ResponseEntity<ApiResponse> handleFolderNotFound(FolderNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                new ApiResponse(HttpStatus.NOT_FOUND.value(),
                                                ApiResponseCode.FOLDER_NOT_FOUND,
                                                ex.getMessage()));
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
                return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(
                                new ApiResponse(HttpStatus.CONTENT_TOO_LARGE.value(),
                                                ApiResponseCode.FILE_TOO_LARGE,
                                                ex.getMessage()));
        }

        @ExceptionHandler(ExpiredJwtException.class)
        public ResponseEntity<ApiResponse> handleExpiredJwt(ExpiredJwtException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                new ApiResponse(HttpStatus.UNAUTHORIZED.value(),
                                                ApiResponseCode.TOKEN_EXPIRED,
                                                ex.getMessage()));
        }

        @ExceptionHandler(InvalidPasswordResetTokenException.class)
        public ResponseEntity<ApiResponse> handleInvalidPasswordResetToken(InvalidPasswordResetTokenException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ApiResponseCode.INVALID_PASSWORD_RESET_TOKEN,
                                                ex.getMessage()));
        }

        @ExceptionHandler(InvalidEmailVerificationTokenException.class)
        public ResponseEntity<ApiResponse> handleInvalidEmailVerificationToken(
                        InvalidEmailVerificationTokenException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                                                ApiResponseCode.INVALID_EMAIL_VERIFICATION_TOKEN,
                                                ex.getMessage()));
        }

        @ExceptionHandler(EmailNotVerifiedException.class)
        public ResponseEntity<ApiResponse> handleEmailNotVerified(
                        EmailNotVerifiedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                                new ApiResponse(HttpStatus.FORBIDDEN.value(),
                                                ApiResponseCode.EMAIL_NOT_VERIFIED,
                                                ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse> handleValidationException(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .collect(Collectors.toMap(
                                                FieldError::getField,
                                                FieldError::getDefaultMessage,
                                                (existing, replacement) -> existing));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                new ApiResponse(
                                                400,
                                                ApiResponseCode.VALIDATION_FAILED,
                                                "Request validation failed.",
                                                errors));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                ApiResponseCode.INTERNAL_SERVER_ERROR,
                                                ex.getMessage()));
        }
}
package com.argha.telestore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.argha.telestore.dto.ApiResponse;
import com.argha.telestore.dto.ApiResponseCode;
import com.argha.telestore.dto.auth.AuthResponse;
import com.argha.telestore.dto.auth.ForgotPasswordRequest;
import com.argha.telestore.dto.auth.LoginRequest;
import com.argha.telestore.dto.auth.LoginResponse;
import com.argha.telestore.dto.auth.ResetPasswordRequest;
import com.argha.telestore.dto.auth.RegisterRequest;
import com.argha.telestore.dto.auth.ResendVerificationRequest;
import com.argha.telestore.exception.InvalidPasswordResetTokenException;
import com.argha.telestore.service.AuthService;
import com.argha.telestore.service.EmailVerificationService;
import com.argha.telestore.service.PasswordResetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final PasswordResetService passwordResetService;
	private final EmailVerificationService emailVerificationService;

	@Value("${app.security.cookie.secure}")
	private boolean isCookieSecure;

	@Value("${app.security.cookie.same-site}")
	private String cookieSameSite;

	public AuthController(AuthService authService, PasswordResetService passwordResetService,
			EmailVerificationService emailVerificationService) {
		this.authService = authService;
		this.passwordResetService = passwordResetService;
		this.emailVerificationService = emailVerificationService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
		authService.register(request);

		ApiResponse response = new ApiResponse(201, ApiResponseCode.REGISTRATION_SUCCESSFUL,
				"Registration Successful");

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/verify-email")
	public ResponseEntity<ApiResponse> verifyEmail(
			@Valid @RequestParam String token) {

		emailVerificationService.verifyEmail(token);

		ApiResponse response = new ApiResponse(200, ApiResponseCode.EMAIL_VERIFIED_SUCCESSFULLY,
				"Email verified successfully");

		return ResponseEntity.ok(response);
	}

	@PostMapping("/resend-verification")
	public ResponseEntity<ApiResponse> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {

		emailVerificationService.resendVerificationEmail(request.getEmail());

		ApiResponse response = new ApiResponse(200, ApiResponseCode.VERIFICATION_EMAIL_SENT,
				"If an unverified account exists with this email, a verification email has been sent.");

		return ResponseEntity.ok(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse authResponse = authService.login(request);

		ResponseCookie refreshTokenCookie = createRefreshTokenCookie(authResponse.getRefreshToken());

		LoginResponse loginResponse = new LoginResponse(authResponse.getToken());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.body(loginResponse);
	}

	@PostMapping("/refresh")
	public ResponseEntity<LoginResponse> refresh(
			@CookieValue(name = "refreshToken", required = false) String oldRefreshToken) {
		AuthResponse authResponse = authService.refresh(oldRefreshToken);

		ResponseCookie refreshTokenCookie = createRefreshTokenCookie(authResponse.getRefreshToken());

		LoginResponse loginResponse = new LoginResponse(authResponse.getToken());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.body(loginResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse> logout(
			@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		authService.logout(refreshToken);

		ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
				.httpOnly(true)
				.secure(isCookieSecure)
				.path("/api/auth")
				.maxAge(0)
				.sameSite(cookieSameSite)
				.build();

		ApiResponse response = new ApiResponse(201, ApiResponseCode.SUCCESS, "Logged out successfully");

		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
				.body(response);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		passwordResetService.forgotPassword(request.getEmail());

		ApiResponse response = new ApiResponse(202, ApiResponseCode.PASSWORD_RESET_EMAIL_SENT,
				"Password reset request accepted");

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse> resetPassword(
			@Valid @RequestBody ResetPasswordRequest request)
			throws InvalidPasswordResetTokenException {
		passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

		ApiResponse response = new ApiResponse(200, ApiResponseCode.PASSWORD_RESET_SUCCESSFUL,
				"Password reset successful");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	private ResponseCookie createRefreshTokenCookie(String token) {
		return ResponseCookie.from("refreshToken", token)
				.httpOnly(true)
				.secure(isCookieSecure)
				.path("/api/auth")
				.maxAge(7 * 24 * 60 * 60) // 7 days
				.sameSite(cookieSameSite)
				.build();
	}
}

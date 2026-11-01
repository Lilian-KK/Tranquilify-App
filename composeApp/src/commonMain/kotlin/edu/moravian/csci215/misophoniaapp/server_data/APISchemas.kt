package edu.moravian.csci215.misophoniaapp.server_data

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class UserCreate (
    val code: String,
    val login_credentials: LoginCredentials,
    val phone: String
)

@Serializable
data class LoginCredentials (
    // constraints: min_length=3, max_length=32
    val username: String,
    // constraints: min_length=8, max_length=72
    val password: String
)

@Serializable
data class UserResponse(
    val username: String,
    val created_at: LocalDateTime
)

@Serializable
data class LoginResponse (
    val access_token: String,
    val refresh_token: String,
    val token_type: String = "Bearer",
    val expires_in: Int = 900
)

@Serializable
data class LoginRequest(
    val username: String,
    val login_method: String, //either "password" or "code"
    val login_value: String
)

@Serializable
data class LogoutRequest(
    val refresh_token: String
)

@Serializable
data class RefreshRequest(
    val refresh_token: String
)

//todo change username and other vals here
@Serializable
data class PhoneAuthPayload(
    val username: String
)

@Serializable
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String
)

@Serializable
data class ResetPasswordRequest(
    val username: String,
    val code: String,
    val new_password: String
)

@Serializable
data class InitialPhoneAuthRequest(
    val phone_number: String
)

@Serializable
data class ChangePhoneRequest(
    val new_number: String,
    val code: String
)

@Serializable
data class ChangePhoneResponse(
    val phone_number: String
)
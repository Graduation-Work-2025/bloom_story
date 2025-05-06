package com.example.bloom.data

// 회원가입 요청 DTO
data class SignUpRequestDto(
    val name: String,
    val nickname: String,
    val user_id: String,
    val password: String,
    val phone: String,
    val character_id: Int
)

// 회원가입 응답 DTO
data class SignUpResponseDto(
    val id: Int,
    val name: String,
    val nickname: String,
    val user_id: String,
    val password: String,
    val phone: String,
    val character_id: Int
)

// 로그인 요청 DTO
data class LoginRequestDto(
    val user_id: String,
    val password: String
)

// 로그인 응답 DTO
data class LoginResponseDto(
    val access_token: String
)
data class UploadImageResponse(
    val imageUrl: String
)
package com.example.bloom.network

import com.example.bloom.data.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ✅ 회원가입
    @POST("users/signup")
    suspend fun signUp(@Body signUpInfo: SignUpRequestDto): Response<Unit>


    // ✅ 로그인
    @POST("users/login")
    suspend fun login(@Body loginInfo: LoginRequestDto): Response<LoginResponseDto>

    // ✅ 내 정보 조회 (토큰 필요)
    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>

    // ✅ 내 정보 수정
    @PUT("users/update")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body updatedInfo: UpdateProfileRequestDto
    ): Response<UserProfileResponse>

    // ✅ 내 스토리 목록 조회
    @GET("story/my")
    suspend fun getMyStories(
        @Header("Authorization") token: String
    ): Response<List<StoryData>>

    // ✅ 스토리 상세 조회
    @GET("story/{id}")
    suspend fun getStoryById(
        @Header("Authorization") token: String,
        @Path("id") storyId: Int
    ): Response<StoryData>

    // ✅ 이미지 업로드
    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>

    // ✅ 유저 ID 기반 조회
    @GET("users/{id}")
    suspend fun getUserProfileById(
        @Path("id") id: Int
    ): Response<UserProfileResponse>

    //지피티 테스트
    @POST("chat-gpt/test")
    suspend fun requestChatGptTest(
        @Query("emotion") emotion: String
    ): Response<String>   // ⚡ 꼭 String으로!





}


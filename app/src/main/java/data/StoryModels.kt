package com.example.bloom.data

// 스토리 작성 요청
data class StoryPostRequest(
    val domain: String = "story",
    val command: String = "create_story",
    val token: String,
    val request: StoryContent
)

data class StoryContent(
    val content: String,
    val longitude: Double,
    val latitude: Double,
    val sharing_type: String,
    val emotion_id: Int,
    val image_url: String
)


// 스토리 작성 응답
data class StoryPostResponse(
    val error: ErrorResponse,
    val response: StoryData
)

data class StoryData(
    val id: Int,
    val content: String,
    val longitude: Double,
    val latitude: Double,
    val likes: Int,
    val user_id: Int,
    val emotion_id: Int,
    val bloom_id: Int,
    val sharing_type: String,
    val image_url: String
)

// 위치 기반 스토리 목록 응답
data class StoryListResponse(
    val error: ErrorResponse,
    val response: StoryListResult
)

data class StoryListResult(
    val stories: List<StoryData>
)

data class FeedFlower(
    val id: Int,
    val imageRes: Int
)


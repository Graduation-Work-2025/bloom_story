package com.example.bloom.data

// 친구 검색 요청
data class FriendSearchRequest(
    val user_id: String
)

// 친구 검색 응답
data class FriendSearchResponse(
    val error: ErrorResponse,
    val response: FriendSearchResult
)

data class FriendSearchResult(
    val friends: List<Friend>
)

data class Friend(
    val id: Int,  // ✅ 여기 쉼표 제대로 고침!
    val user_id: String,
    val name: String,
    val is_friend: Boolean
)

// 친구 목록/대기 목록 응답용
data class FriendListResponse(
    val error: ErrorResponse,
    val response: FriendList
)

data class FriendList(
    val friendships: List<FriendSimple>
)

data class FriendSimple(
    val id: Int,
    val user_id: String
)

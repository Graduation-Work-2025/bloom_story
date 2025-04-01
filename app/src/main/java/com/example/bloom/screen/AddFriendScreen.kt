package com.example.bloom.screen

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.data.*
import com.example.bloom.network.WebSocketManager
import com.example.bloom.util.PreferenceManager
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

@Composable
fun AddFriendScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Friend>>(emptyList()) }
    var addedFriends by remember { mutableStateOf(mutableSetOf<Int>()) }

    var selectedTab by remember { mutableStateOf(0) }

    var pendingFriends by remember { mutableStateOf<List<FriendSimple>>(emptyList()) }
    var currentFriends by remember { mutableStateOf<List<FriendSimple>>(emptyList()) }


    val gson = remember { Gson() }
    val token = PreferenceManager.getAccessToken()

    // 🔸 검색 기능
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2 && token != null) {
            val request = FriendSearchRequest(user_id = searchQuery)
            val json = gson.toJson(mapOf(
                "domain" to "friend",
                "command" to "search_friend",
                "token" to token,
                "request" to request
            ))
            WebSocketManager.connect(object : WebSocketListener() {
                override fun onOpen(ws: WebSocket, resp: Response) {
                    ws.send(json)
                }

                override fun onMessage(ws: WebSocket, text: String) {
                    val res = gson.fromJson(text, FriendSearchResponse::class.java)
                    Handler(Looper.getMainLooper()).post {
                        searchResults = res.response.friends
                    }
                }
            })
        } else {
            searchResults = emptyList()
        }
    }

    // 🔸 친구 목록 조ㅇ회
    LaunchedEffect(Unit) {
        token?.let {
            val json = gson.toJson(mapOf(
                "domain" to "friend",
                "command" to "get_friendships",
                "token" to it,
                "request" to mapOf<String, String>()
            ))
            WebSocketManager.connect(object : WebSocketListener() {
                override fun onOpen(ws: WebSocket, resp: Response) {
                    ws.send(json)
                }

                override fun onMessage(ws: WebSocket, text: String) {
                    val res = gson.fromJson(text, FriendListResponse::class.java)
                    Handler(Looper.getMainLooper()).post {
                        currentFriends = res.response.friendships
                    }
                }
            })
        }
    }

    // 🔸 대기 목록 조회
    LaunchedEffect(Unit) {
        token?.let {
            val json = gson.toJson(mapOf(
                "domain" to "friend",
                "command" to "get_pending_friendships",
                "token" to it,
                "request" to mapOf<String, String>()
            ))
            WebSocketManager.connect(object : WebSocketListener() {
                override fun onOpen(ws: WebSocket, resp: Response) {
                    ws.send(json)
                }

                override fun onMessage(ws: WebSocket, text: String) {
                    val res = gson.fromJson(text, FriendListResponse::class.java)
                    Handler(Looper.getMainLooper()).post {
                        pendingFriends = res.response.friendships
                    }
                }
            })
        }
    }

    // 🔸 UI 구성
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.Close, contentDescription = "닫기")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("친구 추가", fontSize = 22.sp, color = Color.Black)
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("아이디 검색") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (searchQuery.isNotEmpty()) {
            Text("검색 결과", fontSize = 18.sp, color = Color.Gray)
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(searchResults) { friend ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(friend.user_id, fontSize = 18.sp)
                        Button(
                            onClick = {
                                if (token != null) {
                                    val json = gson.toJson(mapOf(
                                        "domain" to "friend",
                                        "command" to "request_friendship",
                                        "token" to token,
                                        "request" to mapOf("friend_id" to friend.id)
                                    ))
                                    WebSocketManager.connect(object : WebSocketListener() {
                                        override fun onOpen(ws: WebSocket, resp: Response) {
                                            ws.send(json)
                                        }

                                        override fun onMessage(ws: WebSocket, text: String) {
                                            addedFriends.add(friend.id)
                                        }
                                    })
                                }
                            },
                            enabled = !addedFriends.contains(friend.id),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F))
                        ) {
                            Text(if (addedFriends.contains(friend.id)) "추가됨" else "친구 추가")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("대기 중인 친구", modifier = Modifier.padding(10.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("친구 목록", modifier = Modifier.padding(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                Text("대기 중인 친구 목록", fontSize = 18.sp, color = Color.Gray)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(pendingFriends) { friend: FriendSimple ->
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(friend.user_id, fontSize = 18.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    val json = gson.toJson(mapOf(
                                        "domain" to "friend",
                                        "command" to "allow_friendship",
                                        "token" to token,
                                        "request" to mapOf("friend_id" to friend.id)
                                    ))
                                    WebSocketManager.connect(object : WebSocketListener() {
                                        override fun onOpen(ws: WebSocket, resp: Response) {
                                            ws.send(json)
                                        }
                                    })
                                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B57C4))) {
                                    Text("수락")
                                }

                                Button(onClick = {
                                    val json = gson.toJson(mapOf(
                                        "domain" to "friend",
                                        "command" to "delete_friendship",
                                        "token" to token,
                                        "request" to mapOf("friend_id" to friend.id)
                                    ))
                                    WebSocketManager.connect(object : WebSocketListener() {
                                        override fun onOpen(ws: WebSocket, resp: Response) {
                                            ws.send(json)
                                        }
                                    })
                                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                                    Text("거절")
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                Text("친구 목록", fontSize = 18.sp, color = Color.Gray)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(currentFriends) { friend: FriendSimple ->
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(friend.user_id, fontSize = 18.sp)
                            Button(onClick = {
                                val json = gson.toJson(mapOf(
                                    "domain" to "friend",
                                    "command" to "delete_friendship",
                                    "token" to token,
                                    "request" to mapOf("friend_id" to friend.id)
                                ))
                                WebSocketManager.connect(object : WebSocketListener() {
                                    override fun onOpen(ws: WebSocket, resp: Response) {
                                        ws.send(json)
                                    }
                                })
                            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                                Text("삭제")
                            }
                        }
                    }
                }
            }
        }
    }
}

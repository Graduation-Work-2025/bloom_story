package com.example.bloom.screen

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.R
import com.example.bloom.data.FeedFlower
import com.example.bloom.data.StoryListResponse
import com.example.bloom.network.WebSocketManager
import com.example.bloom.util.PreferenceManager
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import com.example.bloom.util.getCharacterResId

fun getFlowerImageForEmotion(emotionId: Int): Int {
    return when (emotionId) {
        1 -> R.drawable.flower1
        2 -> R.drawable.flower2
        3 -> R.drawable.flower3
        4 -> R.drawable.flower4
        5 -> R.drawable.flower5
        6 -> R.drawable.flower6
        7 -> R.drawable.flower7
        else -> R.drawable.flower_default
    }
}



@Composable
fun MainScreen(
    navController: NavController,
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val gson = remember { Gson() }
    val context = LocalContext.current
    var feedList by remember { mutableStateOf<List<FeedFlower>>(emptyList()) }

    val token = PreferenceManager.getAccessToken()
    val characterId = PreferenceManager.getCharacterId()

    Log.d("MainScreen", "✅ 불러온 토큰: $token")
    Log.d("MainScreen", "✅ 불러온 캐릭터 ID: $characterId")

    // 서버에서 감정별 피드 불러오기
    LaunchedEffect(Unit) {
        if (token == null) return@LaunchedEffect
        val request = mapOf(
            "domain" to "story",
            "command" to "get_stories",
            "token" to token,
            "request" to mapOf(
                "longitude" to 127.0,
                "latitude" to 37.0
            )
        )
        val json = gson.toJson(request)

        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val response = gson.fromJson(text, StoryListResponse::class.java)
                val stories = response.response.stories
                feedList = stories.map {
                    val flowerRes = getFlowerImageForEmotion(it.emotion_id)
                    FeedFlower(it.id, flowerRes)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        WebSocketManager.connect(listener)
        WebSocketManager.send(json)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        // 🔸 내 캐릭터 이미지 표시
        characterId?.let {
            Image(
                painter = painterResource(id = getCharacterResId(it)),
                contentDescription = "내 캐릭터",
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(feedList) { flower ->
                Image(
                    painter = painterResource(id = flower.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .padding(4.dp)
                )
            }
        }

        // 🔹 메뉴 버튼
        Box {
            IconButton(
                onClick = { menuExpanded = !menuExpanded },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "메뉴",
                    modifier = Modifier.size(60.dp)
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.width(250.dp)
            ) {
                DropdownMenuItem(text = { Text("감정 달력", fontSize = 18.sp) }, onClick = { menuExpanded = false })
                DropdownMenuItem(text = { Text("친구", fontSize = 18.sp) }, onClick = {
                    menuExpanded = false
                    navController.navigate("add_friend")
                })
                DropdownMenuItem(text = { Text("설정", fontSize = 18.sp) }, onClick = { menuExpanded = false })
                DropdownMenuItem(text = { Text("글 목록", fontSize = 18.sp) }, onClick = {
                    menuExpanded = false
                    navController.navigate("post_list")
                })
                DropdownMenuItem(text = { Text("내 정보 수정", fontSize = 18.sp) }, onClick = {
                    menuExpanded = false
                    navController.navigate("edit_profile")
                })
                DropdownMenuItem(text = { Text("감정 정원", fontSize = 18.sp) }, onClick = {
                    menuExpanded = false
                    navController.navigate("emotion_garden")
                })
                DropdownMenuItem(text = { Text("다마고치", fontSize = 18.sp) }, onClick = {
                    menuExpanded = false
                    navController.navigate("tamagotchi")
                })
                DropdownMenuItem(text = { Text("오늘 감정", fontSize = 18.sp) }, onClick = { menuExpanded = false })
            }
        }

        // 🔹 글쓰기 버튼
        IconButton(
            onClick = { navController.navigate("create_post") },
            modifier = Modifier.size(60.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "글 작성",
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

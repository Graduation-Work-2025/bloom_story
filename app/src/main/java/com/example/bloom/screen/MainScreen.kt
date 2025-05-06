package com.example.bloom.screen

import android.os.Handler
import android.os.Looper
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
import com.example.bloom.util.getCharacterResId
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

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
    val tabs = listOf("홈", "추천", "정원")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {},
                        label = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                color = if (selectedTabIndex == index) Color(0xFF55996F) else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0x2255996F) // 연한 초록색 하이라이트
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (selectedTabIndex) {
                0 -> MainFeedContent()
                1 -> ChatGptTestScreen(navController)
                2 -> MyFeedGardenScreen(navController)
            }
        }
    }
}

@Composable
fun MainFeedContent() {
    val context = LocalContext.current
    val gson = remember { Gson() }
    var feedList by remember { mutableStateOf<List<FeedFlower>>(emptyList()) }
    val token = PreferenceManager.getAccessToken()
    val characterId = PreferenceManager.getCharacterId()

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
        verticalArrangement = Arrangement.Top
    ) {
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
    }
}

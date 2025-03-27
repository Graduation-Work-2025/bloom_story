package com.example.bloom.screen
import com.example.bloom.components.BackButton
import com.example.bloom.util.PreferenceManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.data.StoryData
import com.example.bloom.data.StoryListResponse
import com.example.bloom.network.WebSocketManager
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

//글 목록
@Composable
fun PostListScreen(navController: NavController) {
    val gson = remember { Gson() }
    val context = LocalContext.current
    var postList by remember { mutableStateOf<List<StoryData>>(emptyList()) }

    // WebSocketListener 정의
    val listener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocketPostList", "서버 응답: $text")
            val response = gson.fromJson(text, StoryListResponse::class.java)
            if (response.error.code == 200) {
                postList = response.response.stories
            } else {
                Toast.makeText(context, "글 목록 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocketPostList", "연결 실패: ${t.message}")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 화면 진입 시 WebSocket 연결 및 요청 전송
    LaunchedEffect(Unit) {
        val token = PreferenceManager.getAccessToken() ?: ""
        Log.d("PostListScreen", "사용 중인 토큰: $token")
        val request = mapOf(
            "domain" to "story",
            "command" to "get_stories",
            "token" to token,
            "request" to mapOf(
                "longitude" to 36.76375150,
                "latitude" to 127.28198290
            )
        )
        val json = gson.toJson(request)
        WebSocketManager.connect(listener)
        WebSocketManager.send(json)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "닫기")
            }
            Text("내가 작성한 글", fontSize = 22.sp, modifier = Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn {
            items(postList) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("post_detail/${post.id}")
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = post.content ?: "(내용 없음)", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "감정 ID: ${post.emotion_id}", fontSize = 14.sp, color = Color.Gray)
                        Text(text = "작성 ID: ${post.id}", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
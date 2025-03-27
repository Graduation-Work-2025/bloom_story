package com.example.bloom.screen

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.bloom.data.StoryContent
import com.example.bloom.data.StoryPostRequest
import com.example.bloom.network.WebSocketManager
import com.example.bloom.util.PreferenceManager
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import com.google.gson.Gson

fun getEmotionId(emotion: String): Int {
    return when (emotion) {
        "😊 행복" -> 1
        "😢 슬픔" -> 2
        "😡 화남" -> 3
        "😂 웃김" -> 4
        "😍 사랑" -> 5
        "😎 여유" -> 6
        "😴 졸림" -> 7
        else -> 0
    }
}

@Composable
fun CreatePostScreen(navController: NavController) {
    var postContent by remember { mutableStateOf("") }
    var selectedEmotion by remember { mutableStateOf("선택 안됨") }
    var selectedPrivacy by remember { mutableStateOf("전체 공개") }
    var selectedImage by remember { mutableStateOf<Int?>(null) }
    var showEmotionPicker by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val gson = remember { Gson() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "닫기", modifier = Modifier.size(30.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("새 스토리 작성", fontSize = 22.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = postContent,
                onValueChange = { postContent = it },
                label = { Text("내용을 입력하세요") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF55996F),
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("감정 선택", fontSize = 18.sp, color = Color.Black)
                Row {
                    Button(onClick = { /* AI 분석 예정 */ }) { Text("AI 분석") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = { showEmotionPicker = true }) { Text("감정 선택") }
                }
            }
            Divider(color = Color.Gray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPrivacyDialog = true }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("공개 범위", fontSize = 18.sp, color = Color.Black)
                Text(selectedPrivacy, fontSize = 18.sp, color = Color.Gray)
            }
            Divider(color = Color.Gray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("이미지 업로드", fontSize = 18.sp, color = Color.Black)
                selectedImage?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = "업로드된 이미지",
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    )
                } ?: Text("선택 안됨", fontSize = 18.sp, color = Color.Gray)
            }
            Divider(color = Color.Gray, thickness = 1.dp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("취소", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    if (postContent.isBlank()) {
                        Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val token = PreferenceManager.getAccessToken() ?: ""

                    val request = StoryPostRequest(
                        token = token,
                        request = StoryContent(
                            content = postContent,
                            longitude = 36.7637515,
                            latitude = 127.2819829,
                            sharing_type = if (selectedPrivacy == "전체 공개") "PUBLIC" else "FRIEND",
                            emotion_id = getEmotionId(selectedEmotion)
                        )
                    )

                    val json = gson.toJson(request)

                    val listener = object : WebSocketListener() {
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            Log.d("WebSocketPost", "✅ WebSocket 연결됨")
                            webSocket.send(json)
                        }

                        override fun onMessage(webSocket: WebSocket, text: String) {
                            Log.d("WebSocketPost", "📩 서버 응답: $text")
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(context, "글 작성 완료!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }

                        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                            Log.e("WebSocketPost", "연결 실패: ${t.message}")
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(context, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    WebSocketManager.connect(listener)
                },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F))
            ) {
                Text("심기", fontSize = 18.sp, color = Color.White)
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("공개 범위 선택") },
            text = {
                Column {
                    listOf("전체 공개", "친구 공개").forEach { option ->
                        Text(
                            text = option,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPrivacy = option
                                    showPrivacyDialog = false
                                }
                                .padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) {
                    Text("닫기")
                }
            }
        )
    }

    if (showEmotionPicker) {
        AlertDialog(
            onDismissRequest = { showEmotionPicker = false },
            title = { Text("감정 선택") },
            text = {
                Column {
                    listOf("😊 행복", "😢 슬픔", "😡 화남", "😂 웃김", "😍 사랑", "😎 여유", "😴 졸림").forEach { emotion ->
                        Text(
                            text = emotion,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedEmotion = emotion
                                    showEmotionPicker = false
                                }
                                .padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showEmotionPicker = false }) {
                    Text("닫기")
                }
            }
        )
    }
}

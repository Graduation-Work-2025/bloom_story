package com.example.bloom.screen

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.R
import com.example.bloom.components.BackButton
import com.example.bloom.data.LoginInfo
import com.example.bloom.data.LoginRequest
import com.example.bloom.data.LoginResponse
import com.example.bloom.network.WebSocketManager
import com.example.bloom.util.PreferenceManager
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

@Composable
fun LoginScreen(navController: NavController) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val gson = remember { Gson() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(navController)

        Spacer(modifier = Modifier.height(10.dp))
        Image(
            painter = painterResource(id = R.drawable.join),
            contentDescription = "로그인 타이틀",
            modifier = Modifier.size(350.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("아이디") },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF55996F),
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            shape = RoundedCornerShape(15.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF55996F),
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                Log.d("WebSocketLogin", "로그인 버튼 클릭됨")

                if (userId.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val loginRequest = LoginRequest(
                    request = LoginInfo(user_id = userId, password = password)
                )
                val json = gson.toJson(loginRequest)

                Log.d("WebSocketLogin", "전송할 JSON: $json")

                val listener = object : WebSocketListener() {
                    override fun onMessage(webSocket: WebSocket, text: String) {
                        Log.d("WebSocketLogin", "✅ WebSocket 연결됨")
                        Log.d("WebSocketLogin", "서버 응답: $text")

                        val response = gson.fromJson(text, LoginResponse::class.java)
                        val mainHandler = Handler(Looper.getMainLooper())
                        mainHandler.post {
                            if (response.error.code == 200) {
                                val accessToken = response.response?.access_token ?: ""
                                if (accessToken.isNotBlank()) {
                                    PreferenceManager.setAccessToken(accessToken)
                                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "로그인 실패: 토큰 없음", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "로그인 실패: ${response.error.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        Log.e("WebSocketLogin", "연결 실패: ${t.message}")
                        val mainHandler = Handler(Looper.getMainLooper())
                        mainHandler.post {
                            Toast.makeText(context, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                //서버 문제 해결시 주석 해제✅✅✅✅✅✅✅✅✅✅✅
                Log.d("WebSocketLogin", "🛰️ 서버 연결 시도 중...")
                WebSocketManager.connect(listener)

                Log.d("WebSocketLogin", "📤 메시지 전송 시도 중...")
                WebSocketManager.send(json)

                // ✅ 서버 없이 테스트용으로 바로 메인으로 이동
                //navController.navigate("main")
            },
            modifier = Modifier.size(250.dp, 60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF55996F),
                contentColor = Color.White
            )
        ) {
            Text("로그인", fontSize = 18.sp)
        }
    }
}

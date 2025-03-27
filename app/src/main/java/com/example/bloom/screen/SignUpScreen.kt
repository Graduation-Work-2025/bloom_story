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
import com.example.bloom.data.SignUpInfo
import com.example.bloom.data.SignUpRequest
import com.example.bloom.data.SignUpResponse
import com.example.bloom.network.WebSocketManager
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val context = LocalContext.current
    val gson = remember { Gson() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(navController)

        Spacer(modifier = Modifier.height(10.dp))
        Image(
            painter = painterResource(id = R.drawable.register),
            contentDescription = "회원가입 타이틀",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("이름") })
        OutlinedTextField(value = nickname, onValueChange = { nickname = it }, label = { Text("닉네임") })
        OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("아이디") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("전화번호") })

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (name.isBlank() || nickname.isBlank() || userId.isBlank() || password.isBlank() || phone.isBlank()) {
                Toast.makeText(context, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val requestBody = SignUpInfo(
                name = name,
                nickname = nickname,
                user_id = userId,
                password = password,
                phone = phone
            )

            val request = SignUpRequest(request = requestBody)
            val json = gson.toJson(request)

            Log.d("WebSocketSignUp", "\uD83D\uDCE4 전송할 JSON: $json")
            Log.d("WebSocketSignUp", "\uD83D\uDE80 연결 시도 중: ${WebSocketManager.SERVER_URL}")

            val listener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("WebSocketSignUp", "✅ 연결 성공: $response")
                    webSocket.send(json)
                    Log.d("WebSocketSignUp", "\uD83D\uDCE4 JSON 메시지 전송 완료")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("WebSocketSignUp", "\uD83D\uDCEC 서버 응답: $text")

                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        val signUpResponse = gson.fromJson(text, SignUpResponse::class.java)
                        if (signUpResponse.error.code == 200) {
                            Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("signup") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "회원가입 실패: ${signUpResponse.error.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("WebSocketSignUp", "❌ 연결 실패: ${t.message}")
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        Toast.makeText(context, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            WebSocketManager.connect(listener)
        },
            modifier = Modifier.size(250.dp, 60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF55996F),
                contentColor = Color.White
            )
        ) {
            Text("회원가입", fontSize = 18.sp)
        }
    }
}

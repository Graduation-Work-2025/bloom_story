package com.example.bloom.screen

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
    var selectedCharacterId by remember { mutableStateOf<Int?>(null) }
    var showCharacterPicker by remember { mutableStateOf(false) }

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

        Button(
            onClick = { showCharacterPicker = true },
            modifier = Modifier.size(250.dp, 60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF55996F),
                contentColor = Color.White
            )
        ) {
            Text("캐릭터 설정", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (name.isBlank() || nickname.isBlank() || userId.isBlank() || password.isBlank() || phone.isBlank() || selectedCharacterId == null) {
                    Toast.makeText(context, "모든 항목과 캐릭터를 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val requestBody = SignUpInfo(
                    name = name,
                    nickname = nickname,
                    user_id = userId,
                    password = password,
                    phone = phone,
                    character_id = selectedCharacterId!!
                )

                val request = SignUpRequest(request = requestBody)
                val json = gson.toJson(request)

                Log.d("WebSocketSignUp", "📤 전송할 JSON: $json")

                val listener = object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        Log.d("WebSocketSignUp", "✅ WebSocket 연결 성공")
                        webSocket.send(json)
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        Log.d("WebSocketSignUp", "📨 서버 응답 원문: $text")

                        try {
                            val res = gson.fromJson(text, SignUpResponse::class.java)
                            Log.d("WebSocketSignUp", "📬 파싱된 응답: ${res.error.code}, ${res.error.message}")

                            Handler(Looper.getMainLooper()).post {
                                if (res.error.code == 200) {
                                    Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                    navController.navigate("login") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "회원가입 실패: ${res.error.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("WebSocketSignUp", "❌ 응답 파싱 실패: ${e.message}")
                        }
                    }


                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        Log.e("WebSocketSignUp", "❌ 연결 실패: ${t.message}")
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                WebSocketManager.connect(listener)
            },
            modifier = Modifier.size(250.dp, 60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F))
        ) {
            Text("회원가입", fontSize = 18.sp, color = Color.White)
        }
    }

    // 🔸 캐릭터 설정 다이얼로그
    if (showCharacterPicker) {
        AlertDialog(
            onDismissRequest = { showCharacterPicker = false },
            title = { Text("캐릭터 선택") },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(400.dp)
                ) {
                    items((1..8).toList()) { id ->
                        val isSelected = selectedCharacterId == id
                        val borderModifier = if (isSelected) Modifier.border(2.dp, Color(0xFF55996F)) else Modifier

                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(120.dp)
                                .then(borderModifier)
                                .clickable {
                                    selectedCharacterId = id
                                    showCharacterPicker = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = getCharacterResId(id)),
                                contentDescription = "캐릭터 $id",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCharacterPicker = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF55996F),
                        contentColor = Color.White
                    )
                ) {
                    Text("닫기")
                }
            }
        )
    }
}

// 🔸 캐릭터 리소스 ID 매핑
fun getCharacterResId(id: Int): Int {
    return when (id) {
        1 -> R.drawable.character1
        2 -> R.drawable.character2
        3 -> R.drawable.character3
        4 -> R.drawable.character4
        5 -> R.drawable.character5
        6 -> R.drawable.character6
        7 -> R.drawable.character7
        8 -> R.drawable.character8
        else -> R.drawable.character1
    }
}

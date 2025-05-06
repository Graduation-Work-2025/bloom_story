package com.example.bloom.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ChatGptTestScreen(navController: NavController) {
    val context = LocalContext.current
    var emotionInput by remember { mutableStateOf("") }
    var responseMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("ChatGPT API 테스트", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = emotionInput,
            onValueChange = { emotionInput = it },
            label = { Text("감정 입력") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.api.requestChatGptTest(emotionInput)
                        if (response.isSuccessful) {
                            val result = response.body()
                            withContext(Dispatchers.Main) {
                                responseMessage = result ?: "응답 없음"
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                responseMessage = "요청 실패: ${response.code()}"
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            responseMessage = "오류: ${e.message}"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("요청 보내기")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = responseMessage,
            fontSize = 18.sp
        )

    }
}

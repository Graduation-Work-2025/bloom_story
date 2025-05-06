package com.example.bloom.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.R
import com.example.bloom.components.BackButton
import com.example.bloom.data.LoginRequestDto
import com.example.bloom.network.RetrofitInstance
import com.example.bloom.util.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(navController: NavController) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

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
            modifier = Modifier.fillMaxWidth(0.85f),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.85f),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (userId.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val loginDto = LoginRequestDto(
                    user_id = userId,
                    password = password
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        Log.d("RetrofitLogin", "로그인 요청: $loginDto")
                        val response = RetrofitInstance.api.login(loginDto)
                        Log.d("RetrofitLogin", "HTTP 응답 코드: ${response.code()}")

                        if (response.isSuccessful) {
                            val accessToken = response.body()?.access_token ?: ""
                            Log.d("RetrofitLogin", "access_token: $accessToken")

                            if (accessToken.isNotBlank()) {
                                // ⭐ 1. Access Token 저장
                                PreferenceManager.setAccessToken(accessToken)

                                // ⭐ 2. 내 정보 조회하고 저장
                                val profileResponse = RetrofitInstance.api.getUserProfile("Bearer $accessToken")
                                if (profileResponse.isSuccessful) {
                                    profileResponse.body()?.let { profile ->
                                        PreferenceManager.setNickname(profile.nickname)
                                        PreferenceManager.setProfileImageUri(profile.profileImage ?: "")
                                        Log.d("RetrofitLogin", "프로필 저장 완료")
                                    }
                                } else {
                                    Log.e("RetrofitLogin", "프로필 조회 실패: ${profileResponse.code()}")
                                }

                                // ⭐ 3. 메인으로 이동
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "토큰이 비어 있습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                val msg = when (response.code()) {
                                    400 -> "비밀번호가 잘못되었습니다."
                                    404 -> "존재하지 않는 아이디입니다."
                                    else -> "로그인 실패: ${response.code()}"
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.size(250.dp, 60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F))
        ) {
            Text("로그인", fontSize = 18.sp, color = Color.White)
        }
    }
}
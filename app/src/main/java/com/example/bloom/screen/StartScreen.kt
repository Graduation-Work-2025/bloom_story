package com.example.bloom.screen
import com.example.bloom.components.BackButton

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.R

// ✅ 첫 시작 화면 (뒤로 가기 버튼 없음)
@Composable
fun StartScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "앱 로고",
            modifier = Modifier.size(300.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.size(250.dp, 60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F), contentColor = Color.White)
        ) {
            Text("로그인", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("signup") },
            modifier = Modifier.size(250.dp, 60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F), contentColor = Color.White)
        ) {
            Text("회원가입", fontSize = 18.sp)
        }
    }
}
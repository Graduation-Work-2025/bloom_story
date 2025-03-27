package com.example.bloom.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.R

@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("내 정보 수정", fontSize = 24.sp, color = Color(0xFF333333))
        Spacer(modifier = Modifier.height(24.dp))

        // 🔵 프로필 이미지
        Image(
            painter = painterResource(id = R.drawable.profile_default), // 기본 이미지
            contentDescription = "내 프로필",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable {
                    Toast.makeText(context, "프로필 이미지 수정 기능은 추후 구현 예정입니다.", Toast.LENGTH_SHORT).show()
                }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔵 이름
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 🔵 닉네임
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("닉네임") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 🔵 전화번호
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("전화번호") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 🔵 저장 버튼
        Button(
            onClick = {
                Toast.makeText(context, "정보가 저장되었습니다. (API 연동 예정)", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // 이전 화면으로
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F))
        ) {
            Text("저장", fontSize = 18.sp, color = Color.White)
        }
    }
}

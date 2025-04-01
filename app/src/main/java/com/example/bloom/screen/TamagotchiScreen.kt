package com.example.bloom.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.R
import androidx.navigation.NavHostController

@Composable
fun TamagotchiScreen(navController: NavController) {

    val tamagotchiImage = R.drawable.tamagotchi_egg // 기본 캐릭터만 보여줌

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // 다마고치 이미지
        Image(
            painter = painterResource(id = tamagotchiImage),
            contentDescription = "Tamagotchi",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 간단한 설명 텍스트
        Text(text = "다마고치가 곧 태어날 거예요!", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // 나가기 버튼
        Button(onClick = { navController.popBackStack() }) {
            Text("나가기")
        }
    }
}

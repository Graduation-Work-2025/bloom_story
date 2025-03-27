package com.example.bloom.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
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

@Composable
fun PostDetailScreen(navController: NavController, postId: Int) {
    var showComments by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // 🔙 뒤로가기
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🖼️ 이미지 (임시)
        Image(
            painter = painterResource(id = R.drawable.placeholder_image),
            contentDescription = "포스트 이미지",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✏️ 내용
        Text(
            text = "이것은 내가 작성한 글입니다. 감정이 담겨 있어요.",
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 😊 감정 및 📅 작성일
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "감정: 😊 행복", fontSize = 14.sp, color = Color.Gray)
            Text(text = "2025.03.27", fontSize = 14.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ❤️ 좋아요 & 💬 댓글 버튼
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* 좋아요 처리 예정 */ }) {
                Icon(Icons.Filled.FavoriteBorder, contentDescription = "좋아요")
            }
            Text("23개", fontSize = 14.sp)

            Spacer(modifier = Modifier.width(24.dp))

            IconButton(onClick = { showComments = !showComments }) {
                Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "댓글")
            }
            Text("댓글 보기", fontSize = 14.sp)
        }

        // 💬 댓글창
        if (showComments) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("댓글 1: 너무 좋네요!", fontSize = 14.sp)
            Text("댓글 2: 감동이에요~", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("댓글을 입력하세요") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

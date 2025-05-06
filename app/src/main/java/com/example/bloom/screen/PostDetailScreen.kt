package com.example.bloom.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.bloom.data.StoryData
import com.example.bloom.network.RetrofitInstance
import com.example.bloom.util.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PostDetailScreen(navController: NavController, postId: Int) {
    val context = LocalContext.current
    val token = PreferenceManager.getAccessToken() ?: ""
    var story by remember { mutableStateOf<StoryData?>(null) }
    var liked by remember { mutableStateOf(false) }

    // 서버 요청
    LaunchedEffect(postId) {
        try {
            val response = RetrofitInstance.api.getStoryById("Bearer $token", postId)
            if (response.isSuccessful) {
                story = response.body()
            } else {
                Toast.makeText(context, "스토리 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "서버 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    story?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // 이미지
            Image(
                painter = rememberAsyncImagePainter(it.image_url),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 내용
            Text(
                text = it.content,
                fontSize = 18.sp,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 감정 / 작성일 (작성일이 없다면 임의 날짜 사용)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("감정 ID: ${it.emotion_id}", fontSize = 16.sp)
                Text("2025.04.24", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 하트 UI
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (liked) "💖" else "🤍",
                    fontSize = 28.sp,
                    modifier = Modifier.clickable {
                        liked = !liked
                        Toast.makeText(
                            context,
                            if (liked) "좋아요를 눌렀어요!" else "좋아요 취소!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}

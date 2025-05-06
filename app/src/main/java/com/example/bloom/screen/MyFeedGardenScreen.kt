package com.example.bloom.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.bloom.R
import com.example.bloom.data.StoryData
import com.example.bloom.network.RetrofitInstance
import com.example.bloom.util.PreferenceManager

@Composable
fun MyFeedGardenScreen(navController: NavController) {
    val context = LocalContext.current
    val token = PreferenceManager.getAccessToken() ?: ""
    val bearerToken = "Bearer $token"

    var myStories by remember { mutableStateOf<List<StoryData>>(emptyList()) }

    val profileImageUri = PreferenceManager.getProfileImageUri()
    val nickname = PreferenceManager.getNickname()?.takeIf { it.isNotBlank() } ?: "익명의 정원사"

    // 서버에서 내 스토리 목록 조회
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getMyStories(bearerToken)
            if (response.isSuccessful) {
                myStories = response.body() ?: emptyList()
            } else {
                Toast.makeText(context, "피드를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // 프로필 정보
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = profileImageUri ?: R.drawable.profile_default
                ),
                contentDescription = "프로필",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nickname,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { navController.navigate("edit_profile") },
                    modifier = Modifier.height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F))
                ) {
                    Text("프로필 편집", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        // 피드
        if (myStories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("아직 심어진 감정이 없어요 🌱", fontSize = 16.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(myStories) { story ->
                    Image(
                        painter = rememberAsyncImagePainter(model = story.image_url),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                navController.navigate("post_detail/${story.id}")
                            }
                    )
                }
            }
        }
    }
}

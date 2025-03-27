package com.example.bloom.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.R

// 감정 이름은 내부에서만 쓰고 화면에 표시 안 함
data class FeedFlower(val id: Int, val emotion: String, val imageRes: Int)

@Composable
fun MyFeedGardenScreen(navController: NavController) {
    val myFeed = remember {
        listOf(
            FeedFlower(1, "행복", R.drawable.flower1),
            FeedFlower(2, "슬픔", R.drawable.flower6),
            FeedFlower(3, "사랑", R.drawable.flower5),
            FeedFlower(4, "화남", R.drawable.flower2),
            FeedFlower(5, "웃김", R.drawable.flower4),
            FeedFlower(6, "졸림", R.drawable.flower7)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC6E9A4)) // 잔디색 배경
            .padding(16.dp)
    ) {
        // 프로필 상단
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_default),
                contentDescription = "프로필 사진",
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.LightGray, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("choon0414", fontSize = 20.sp, color = Color.Black)
                Text("54 Posts  •  834 Friends", fontSize = 14.sp, color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 감정 정원 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                items(myFeed) { flower ->
                    GardenTile(flower)
                }
            }
        )
    }
}

@Composable
fun GardenTile(flower: FeedFlower) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color(0xFF7DB249), RoundedCornerShape(10.dp))
            .padding(6.dp)
    ) {
        Image(
            painter = painterResource(id = flower.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

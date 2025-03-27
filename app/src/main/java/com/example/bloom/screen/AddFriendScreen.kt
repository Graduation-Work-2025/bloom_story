package com.example.bloom.screen

import com.example.bloom.components.BackButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AddFriendScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var friendsList by remember { mutableStateOf(listOf("choon0414", "karennne", "martini_rond", "andrewww_")) }
    var addedFriends by remember { mutableStateOf(mutableSetOf<String>()) }
    var selectedTab by remember { mutableStateOf(0) } // 0: 대기 중인 친구, 1: 친구 목록
    val pendingRequests = listOf("yuna_dev", "kimminji")
    val currentFriends = listOf("bestie_01", "friend_lee", "cool_guy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 뒤로가기 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "닫기")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("친구 추가", fontSize = 22.sp, color = Color.Black)
        }

        // 🔹 검색창
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("아이디 검색") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 검색 결과
        if (searchQuery.isNotEmpty()) {
            Text("검색 결과", fontSize = 18.sp, color = Color.Gray)
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(friendsList.filter { it.contains(searchQuery, ignoreCase = true) }) { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(friend, fontSize = 18.sp, color = Color.Black)
                        Button(
                            onClick = { addedFriends.add(friend) },
                            enabled = !addedFriends.contains(friend),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F), contentColor = Color.White)
                        ) {
                            Text(if (addedFriends.contains(friend)) "추가됨" else "친구 추가")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🔹 탭 메뉴
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("대기 중인 친구", modifier = Modifier.padding(10.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("친구 목록", modifier = Modifier.padding(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                Text("대기 중인 친구 목록", fontSize = 18.sp, color = Color.Gray)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(pendingRequests) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(user, fontSize = 18.sp, color = Color.Black)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { /* 수락 로직 */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B57C4))) {
                                    Text("수락")
                                }
                                Button(onClick = { /* 거절 로직 */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                                    Text("거절")
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                Text("친구 목록", fontSize = 18.sp, color = Color.Gray)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(currentFriends) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(user, fontSize = 18.sp, color = Color.Black)
                            Button(onClick = { /* 삭제 로직 */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                                Text("삭제")
                            }
                        }
                    }
                }
            }
        }
    }
}

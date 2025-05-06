package com.example.bloom.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import coil.compose.rememberAsyncImagePainter
import com.example.bloom.R
import com.example.bloom.components.BackButton
import com.example.bloom.data.UpdateProfileRequestDto
import com.example.bloom.network.RetrofitInstance
import com.example.bloom.util.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var shouldSave by remember { mutableStateOf(false) }

    val token = PreferenceManager.getAccessToken()
    val characterId = PreferenceManager.getCharacterId() ?: 1

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    // 🔁 사용자 정보 불러오기
    LaunchedEffect(Unit) {
        try {
            val bearerToken = "Bearer $token"
            val response = RetrofitInstance.api.getUserProfile(bearerToken)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    name = user.name
                    nickname = user.nickname
                    phone = user.phone
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "불러오기 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔁 저장 요청
    LaunchedEffect(shouldSave) {
        if (shouldSave) {
            shouldSave = false

            val bearerToken = "Bearer $token"
            val request = UpdateProfileRequestDto(
                name = name,
                nickname = nickname,
                password = "test1234",
                phone = phone,
                character_id = characterId
            )

            try {
                val response = RetrofitInstance.api.updateUserProfile(bearerToken, request)
                if (response.isSuccessful) {
                    PreferenceManager.setNickname(nickname)
                    selectedImageUri?.let { uri ->
                        PreferenceManager.setProfileImageUri(uri.toString())
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "수정 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "수정 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔙 완전 좌측 상단에 붙은 뒤로가기 버튼
        BackButton(
            navController = navController,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 4.dp, top = 10.dp)
        )

        // 🧱 나머지 UI 중앙 정렬 + padding 적용
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                when {
                    selectedImageUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "선택된 프로필 이미지",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    !PreferenceManager.getProfileImageUri().isNullOrBlank() -> {
                        Image(
                            painter = rememberAsyncImagePainter(PreferenceManager.getProfileImageUri()),
                            contentDescription = "저장된 프로필",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.profile_default),
                            contentDescription = "기본 프로필",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("이름") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("전화번호") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { shouldSave = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF55996F))
            ) {
                Text("저장", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

package com.example.bloom.navigation

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bloom.screen.*

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            if (!granted) {
                Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    NavHost(navController = navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("main") { MainScreen(navController, requestPermissionLauncher) }
        composable("create_post") { CreatePostScreen(navController) }
        composable("edit_profile") { EditProfileScreen(navController) }
        composable("emotion_garden") { MyFeedGardenScreen(navController) }
        composable("chatgpttest") { ChatGptTestScreen(navController) }


        // ✅ 상세 글 보기 (PostDetail)
        composable("post_detail/{storyId}") { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId")?.toIntOrNull()
            storyId?.let {
                PostDetailScreen(navController, it)
            }
        }

    }
}

package com.example.bloom.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BackButton(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "닫기",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

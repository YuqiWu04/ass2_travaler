package com.example.ass2_travaler.componentTool

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay
import androidx.compose.material3.Icon
@Composable
fun LikeIconButton(
    onClick: () -> Unit
) {
    var liked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (liked) 1.5f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    IconButton(
        onClick = {
            liked = true
            onClick()
        },
        modifier = Modifier.scale(scale)
    ) {
        Icon(
            imageVector = Icons.Default.ThumbUp,
            contentDescription = "Like",
            tint = MaterialTheme.colorScheme.primary
        )
    }


    LaunchedEffect(liked) {
        if (liked) {
            delay(300)
            liked = false
        }
    }
}
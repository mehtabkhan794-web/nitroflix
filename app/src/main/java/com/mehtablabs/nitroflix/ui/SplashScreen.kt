package com.mehtablabs.nitroflix.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }
    
    // Heartbeat Pulse Animation for "Flix"
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val pulseGlowBlur by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        // Loading animation duration
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
        )
        delay(200)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Smoky Background Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        0f to Color(0xFF1C1C1C),
                        0.7f to Color(0xFF0A0A0A),
                        1f to Color.Black
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main Branding: Nitro (Red) + Flix (White Pulse)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // "Nitro" with sharp red shadow
                Text(
                    text = "Nitro",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 120.sp,
                        shadow = Shadow(
                            color = Color(0xFFFF3D00).copy(alpha = 0.8f),
                            offset = Offset(0f, 0f),
                            blurRadius = 35f
                        )
                    ),
                    color = Color(0xFFFF3D00),
                    letterSpacing = (-6).sp
                )
                
                // "Flix" with pulse and breathing glow
                Text(
                    text = "Flix",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 120.sp,
                        shadow = Shadow(
                            color = Color.White.copy(alpha = 0.6f),
                            offset = Offset(0f, 0f),
                            blurRadius = pulseGlowBlur
                        )
                    ),
                    color = Color.White,
                    letterSpacing = (-6).sp,
                    modifier = Modifier.scale(pulseScale)
                )
            }

            // Tagline - matches image casing and style
            Text(
                text = "Product of MehtabLabs",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Normal,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            
            Spacer(modifier = Modifier.height(120.dp))

            // Minimalist Cinematic Progress Bar
            Box(
                modifier = Modifier
                    .width(400.dp)
                    .height(3.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.value)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFFFF3D00), Color(0xFFFF8E53))
                            ),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

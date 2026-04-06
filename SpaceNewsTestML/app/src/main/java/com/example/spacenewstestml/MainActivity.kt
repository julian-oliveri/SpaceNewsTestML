package com.example.spacenewstestml

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.spacenewstestml.navigation.AppNavigation
import com.example.spacenewstestml.ui.theme.SpaceNewsTestMLTheme
import com.example.spacenewstestml.ui.articles.ArticleScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        window.setBackgroundDrawableResource(android.R.color.black)

        setContent {
            SpaceNewsTestMLTheme {
                Box(modifier = Modifier.fillMaxSize()) {

                    StarBackground()

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Transparent
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

// Fondo de estrellas (150 puntos al azar)
@Composable
fun StarBackground() {
    val random = remember { Random(System.currentTimeMillis()) }

    val stars = remember {
        List(150) {
            Triple(
                random.nextFloat(), // x
                random.nextFloat(), // y
                random.nextFloat()  // phase (se usa para la animacion)
            )
        }
    }

    // Animacion "twinkle" se comenta por ahora
//    val infiniteTransition = rememberInfiniteTransition(label = "stars")
//
//    val twinkle by infiniteTransition.animateFloat(
//        initialValue = 0.85f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(5000),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "twinkle"
//    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        stars.forEach { (x, y, phase) ->
//            val baseAlpha = 0.3f + random.nextFloat() * 0.7f
//            val alphaOffset = 0.9f + (phase * 0.1f)

            drawCircle(
//                color = Color.White.copy(alpha = baseAlpha * twinkle * alphaOffset),
                color = Color.White.copy(),
                radius = 1.5f,
                center = Offset(x * width, y * height)
            )
        }
    }
}

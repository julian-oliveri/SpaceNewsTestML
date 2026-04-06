package com.example.spacenewstestml.ui.articledetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.spacenewstestml.R
import com.example.spacenewstestml.data.models.Article
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ArticleDetailScreen(
    article: Article,
    onBack: () -> Unit,
    onReadFullArticle: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    type: String,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Box {
            with(sharedTransitionScope) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(article.imageUrl)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .crossfade(true)
                            .placeholder(R.mipmap.planet)
                            .error(R.mipmap.planet)
                            .build(),
                        contentDescription = article.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "image-${type}-${article.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(8.dp)
                    .statusBarsPadding()
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(12.dp))

            article.authors.forEach { author ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Por ${author.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFEFB434)
                    )

                    val socials = author.socials?.toList().orEmpty()
                    val visibleSocials = socials.take(3)
                    val remainingCount = socials.size - visibleSocials.size

                    visibleSocials.forEach { (label, url) ->
                        SocialMediaBadge(label = label, url = url, context = context)
                    }

                    if (remainingCount > 0) {
                        MoreSocialsBadge(
                            name = author.name,
                            count = remainingCount,
                            socials = socials,
                            context = context
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = article.publishedAt,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onReadFullArticle(article.url) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEFB434),
                    contentColor = Color.Black
                )

            ) {
                Text("Lee el articulo completo en: ${article.newsSite}")
            }

            Spacer(Modifier.height(50.dp))
        }
    }
}

// Colores para redes
private val socialColors = mapOf(
    "X" to Color(0xFF000000),
    "YouTube" to Color(0xFFFF0000),
    "Instagram" to Color(0xFFE1306C),
    "LinkedIn" to Color(0xFF0A66C2),
    "Mastodon" to Color(0xFF6364FF),
    "Bluesky" to Color(0xFF0085FF)
)

// Iconos (ya existentes)
private val socialIcons = mapOf(
    "X" to SocialIcon.Vector(Icons.Default.Close),
    "YouTube" to SocialIcon.Vector(Icons.Default.PlayArrow),
    "Instagram" to SocialIcon.Drawable(R.drawable.camera_drawable),
    "LinkedIn" to SocialIcon.Vector(Icons.Default.AccountBox),
    "Mastodon" to SocialIcon.Drawable(R.drawable.unknown_social),
    "Bluesky" to SocialIcon.Drawable(R.drawable.unknown_social)
)

sealed class SocialIcon {
    data class Vector(val imageVector: ImageVector) : SocialIcon()
    data class Drawable(val resId: Int) : SocialIcon()
}

@Composable
private fun SocialMediaBadge(label: String, url: String, context: Context) {
    val color = socialColors[label] ?: MaterialTheme.colorScheme.primary
    val icon = socialIcons[label]

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        when (icon) {
            is SocialIcon.Vector -> {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            is SocialIcon.Drawable -> {
                Icon(
                    painter = painterResource(id = icon.resId),
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            null -> {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun MoreSocialsBadge(
    name: String,
    count: Int,
    socials: List<Pair<String, String>>,
    context: Context
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Text("+$count")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            title = { Text("Redes sociales de $name") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    socials.forEach { (label, url) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            SocialMediaBadge(
                                label = label,
                                url = url,
                                context = context
                            )

                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        )
    }
}
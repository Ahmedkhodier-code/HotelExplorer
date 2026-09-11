package com.khodier.hotelexplorer.navigation

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodier.hotelexplorer.core.designsystem.theme.HotelExplorerTheme

@Composable
fun BottomNav(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val faintColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Surface(
        shadowElevation = 16.dp,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(68.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isActive = currentRoute == item.screen.route

                val iconColor by animateColorAsState(
                    targetValue = if (isActive) primaryColor else faintColor,
                    animationSpec = tween(150),
                    label = "iconColor"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isActive) primaryColor else faintColor,
                    animationSpec = tween(150),
                    label = "labelColor"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onNavigate(item.screen) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                                    .background(primaryColor)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val labelText = stringResource(item.label)

                    Icon(
                        imageVector = item.icon,
                        contentDescription = labelText,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = labelText,
                        fontSize = 10.sp,
                        color = labelColor,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BottomNavPreview() {
    HotelExplorerTheme {
        BottomNav(
            currentRoute = Screen.Favorites.route,
            onNavigate = {}
        )
    }
}

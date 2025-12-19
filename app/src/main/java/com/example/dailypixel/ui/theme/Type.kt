package com.example.dailypixel.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.dailypixel.R

// Custom pixel art fonts
val PixellariFontFamily = FontFamily(
    Font(R.font.pixellari, FontWeight.Normal)
)

val RainyHeartsFontFamily = FontFamily(
    Font(R.font.rainyhearts, FontWeight.Normal)
)

// Pixel art typography using custom fonts
val Typography = Typography(
    // Display styles - for big titles
    displayLarge = TextStyle(
        fontFamily = PixellariFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = 2.sp
    ),
    displayMedium = TextStyle(
        fontFamily = PixellariFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = 1.5.sp
    ),

    // Headline styles - for section titles
    headlineLarge = TextStyle(
        fontFamily = PixellariFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 1.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PixellariFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 1.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PixellariFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),

    // Title styles
    titleLarge = TextStyle(
        fontFamily = PixellariFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RainyHeartsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RainyHeartsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.25.sp
    ),

    // Body styles - for regular text
    bodyLarge = TextStyle(
        fontFamily = RainyHeartsFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RainyHeartsFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RainyHeartsFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    // Label styles - for buttons and small UI elements
    labelLarge = TextStyle(
        fontFamily = RainyHeartsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RainyHeartsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RainyHeartsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    )
)
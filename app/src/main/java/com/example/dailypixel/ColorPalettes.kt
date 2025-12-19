package com.example.dailypixel

import androidx.compose.ui.graphics.Color

data class PixelPalette(
    val name: String,
    val colors: List<Color>
)

object ColorPalettes {
    val gameboy = PixelPalette(
        name = "Game Boy",
        colors = listOf(
            Color(0xFF0F380F), // Dark green
            Color(0xFF306230), // Medium dark green
            Color(0xFF8BAC0F), // Medium green
            Color(0xFF9BBD0F), // Light green
            Color(0xFFC4CFA1), // Very light green
            Color(0xFFE7F7E7), // Cream green
            Color(0xFF98CB98), // Soft green
            Color(0xFF68A968)  // Forest green
        )
    )

    val neon = PixelPalette(
        name = "Neon",
        colors = listOf(
            Color(0xFF000814), // Deep navy
            Color(0xFF001D3D), // Dark blue
            Color(0xFF003566), // Medium blue
            Color(0xFFFF006E), // Hot pink
            Color(0xFF8338EC), // Purple
            Color(0xFF3A86FF), // Bright blue
            Color(0xFF06FFA5), // Neon green
            Color(0xFFFFBE0B)  // Electric yellow
        )
    )

    val sunset = PixelPalette(
        name = "Sunset",
        colors = listOf(
            Color(0xFF2D1B69), // Deep purple
            Color(0xFF11151C), // Dark navy
            Color(0xFF7209B7), // Purple
            Color(0xFFE85D04), // Orange
            Color(0xFFFFAA00), // Amber
            Color(0xFFFFBA08), // Golden yellow
            Color(0xFFFF6B6B), // Coral
            Color(0xFFFFE66D)  // Light yellow
        )
    )

    val ocean = PixelPalette(
        name = "Ocean",
        colors = listOf(
            Color(0xFF03045E), // Deep navy
            Color(0xFF023E8A), // Dark blue
            Color(0xFF0077B6), // Medium blue
            Color(0xFF0096C7), // Light blue
            Color(0xFF00B4D8), // Sky blue
            Color(0xFF48CAE4), // Light cyan
            Color(0xFF90E0EF), // Very light blue
            Color(0xFFCAF0F8)  // Almost white blue
        )
    )

    val forest = PixelPalette(
        name = "Forest",
        colors = listOf(
            Color(0xFF2D3748), // Dark gray
            Color(0xFF1A202C), // Very dark brown
            Color(0xFF22543D), // Dark green
            Color(0xFF2F855A), // Medium green
            Color(0xFF48BB78), // Light green
            Color(0xFF9AE6B4), // Very light green
            Color(0xFF8B4513), // Brown
            Color(0xFFDEB887)  // Tan
        )
    )

    val candy = PixelPalette(
        name = "Candy",
        colors = listOf(
            Color(0xFFF72585), // Hot pink
            Color(0xFFB5179E), // Purple pink
            Color(0xFF7209B7), // Purple
            Color(0xFF480CA8), // Dark purple
            Color(0xFF3A0CA3), // Blue purple
            Color(0xFF3F37C9), // Blue
            Color(0xFF4CC9F0), // Light blue
            Color(0xFF7209B7)  // Purple accent
        )
    )

    val cyberpunk = PixelPalette(
        name = "Cyberpunk",
        colors = listOf(
            Color(0xFF0A0A0A), // Black
            Color(0xFF1A1A2E), // Dark blue
            Color(0xFF16213E), // Navy
            Color(0xFF0F3460), // Medium blue
            Color(0xFF533A7B), // Purple
            Color(0xFFE94560), // Red
            Color(0xFFFF2E63), // Hot pink
            Color(0xFF00F5FF)  // Cyan
        )
    )

    val retro8bit = PixelPalette(
        name = "8-Bit",
        colors = listOf(
            Color(0xFF000000), // Black
            Color(0xFFFFFFFF), // White
            Color(0xFFFF0040), // Red
            Color(0xFF131313), // Dark gray
            Color(0xFF1B1B1B), // Medium gray
            Color(0xFF8B956D), // Olive
            Color(0xFFC4CFA1), // Light green
            Color(0xFF4A4A4A)  // Gray
        )
    )

    // 16-color palette for testing
    val rainbow16 = PixelPalette(
        name = "Rainbow 16",
        colors = listOf(
            Color(0xFF000000), // Black
            Color(0xFF444444), // Dark gray
            Color(0xFF888888), // Gray
            Color(0xFFCCCCCC), // Light gray
            Color(0xFFFF0000), // Red
            Color(0xFFFF8800), // Orange
            Color(0xFFFFFF00), // Yellow
            Color(0xFF88FF00), // Lime
            Color(0xFF00FF00), // Green
            Color(0xFF00FF88), // Spring green
            Color(0xFF00FFFF), // Cyan
            Color(0xFF0088FF), // Sky blue
            Color(0xFF0000FF), // Blue
            Color(0xFF8800FF), // Purple
            Color(0xFFFF00FF), // Magenta
            Color(0xFFFF0088)  // Hot pink
        )
    )

    val pastel16 = PixelPalette(
        name = "Soft Pastels",
        colors = listOf(
            Color(0xFF2C2C2C), // Dark gray
            Color(0xFFFFB3BA), // Pink
            Color(0xFFFFDFBA), // Peach
            Color(0xFFFFFFBA), // Light yellow
            Color(0xFFBAFFC9), // Light green
            Color(0xFFBAE1FF), // Light blue
            Color(0xFFE6BAFF), // Light purple
            Color(0xFFFFBAF3), // Light magenta
            Color(0xFFFFC9C9), // Rose
            Color(0xFFC9BAFF), // Lavender
            Color(0xFFBAFFD1), // Mint
            Color(0xFFBAD1FF), // Sky
            Color(0xFFFFD1BA), // Cream
            Color(0xFFD1BAFF), // Violet
            Color(0xFFFFBAD1), // Blush
            Color(0xFFBAFFE6)  // Ice
        )
    )

    val earth16 = PixelPalette(
        name = "Earth Tones",
        colors = listOf(
            Color(0xFF1A1A1A), // Deep black
            Color(0xFF8B4513), // Saddle brown
            Color(0xFFA0522D), // Sienna
            Color(0xFFD2691E), // Chocolate
            Color(0xFFDEB887), // Burlywood
            Color(0xFFF4A460), // Sandy brown
            Color(0xFF2F4F2F), // Dark olive
            Color(0xFF556B2F), // Olive drab
            Color(0xFF808000), // Olive
            Color(0xFF9ACD32), // Yellow green
            Color(0xFF8FBC8F), // Dark sea green
            Color(0xFF98FB98), // Pale green
            Color(0xFF4A4A4A), // Dark gray
            Color(0xFF696969), // Dim gray
            Color(0xFF778899), // Light slate gray
            Color(0xFFF5F5DC)  // Beige
        )
    )

    // Professional pixel art palettes with proper ramps
    val endesga32 = PixelPalette(
        name = "Endesga 32",
        colors = listOf(
            Color(0xFFBE4A2F), // Red dark
            Color(0xFFD77643), // Red light
            Color(0xFFEAD4AA), // Skin light
            Color(0xFFE4A672), // Skin mid
            Color(0xFFB86F50), // Skin dark
            Color(0xFF733E39), // Brown dark
            Color(0xFF3E2731), // Brown darker
            Color(0xFFA22633), // Red mid
            Color(0xFFE43B44), // Red bright
            Color(0xFFF77622), // Orange
            Color(0xFFFEAE34), // Yellow
            Color(0xFFFEE761), // Yellow light
            Color(0xFF63C74D), // Green
            Color(0xFF3E8948), // Green dark
            Color(0xFF265C42), // Green darker
            Color(0xFF193C3E)  // Teal dark
        )
    )

    val pico8 = PixelPalette(
        name = "PICO-8",
        colors = listOf(
            Color(0xFF000000), // Black
            Color(0xFF1D2B53), // Dark blue
            Color(0xFF7E2553), // Dark purple
            Color(0xFF008751), // Dark green
            Color(0xFFAB5236), // Brown
            Color(0xFF5F574F), // Dark gray
            Color(0xFFC2C3C7), // Light gray
            Color(0xFFFFF1E8), // White
            Color(0xFFFF004D), // Red
            Color(0xFFFFA300), // Orange
            Color(0xFFFFEC27), // Yellow
            Color(0xFF00E436), // Green
            Color(0xFF29ADFF), // Blue
            Color(0xFF83769C), // Indigo
            Color(0xFFFF77A8), // Pink
            Color(0xFFFFCCAA)  // Peach
        )
    )

    val lospec500 = PixelPalette(
        name = "Lospec GB",
        colors = listOf(
            Color(0xFF081820), // Darkest
            Color(0xFF346856), // Dark green
            Color(0xFF88C070), // Mid green
            Color(0xFFE0F8D0), // Light green
            Color(0xFF2B3A40), // Dark blue gray
            Color(0xFF4A6B5C), // Mid gray green
            Color(0xFF7BA584), // Light gray green
            Color(0xFFB8D8B0)  // Very light green
        )
    )

    val vinik24 = PixelPalette(
        name = "Vinik 24",
        colors = listOf(
            Color(0xFF000000), // Black
            Color(0xFF6F6776), // Gray dark
            Color(0xFF9A9A97), // Gray mid
            Color(0xFFC5CCBA), // Gray light
            Color(0xFF8B5580), // Purple dark
            Color(0xFFC38890), // Purple light
            Color(0xFFA675FE), // Lavender
            Color(0xFFCE9248), // Gold dark
            Color(0xFFE8C170), // Gold light
            Color(0xFF79A457), // Green mid
            Color(0xFFAAD66F), // Green light
            Color(0xFF44891A), // Green dark
            Color(0xFF2A584F), // Teal dark
            Color(0xFF005E7A), // Blue dark
            Color(0xFF0084A5), // Blue mid
            Color(0xFF3AA5DC)  // Blue light
        )
    )

    val sweetie16 = PixelPalette(
        name = "Sweetie 16",
        colors = listOf(
            Color(0xFF1A1C2C), // Black blue
            Color(0xFF5D275D), // Purple dark
            Color(0xFFB13E53), // Red dark
            Color(0xFFEF7D57), // Orange
            Color(0xFFFFCD75), // Yellow
            Color(0xFFA7F070), // Light green
            Color(0xFF38B764), // Green
            Color(0xFF257179), // Teal dark
            Color(0xFF29366F), // Blue dark
            Color(0xFF3B5DC9), // Blue
            Color(0xFF41A6F6), // Sky blue
            Color(0xFF73EFF7), // Cyan
            Color(0xFFF4F4F4), // White
            Color(0xFF94B0C2), // Light blue gray
            Color(0xFF566C86), // Blue gray
            Color(0xFF333C57)  // Dark blue gray
        )
    )

    val journeyPalette = PixelPalette(
        name = "Journey",
        colors = listOf(
            Color(0xFF050914), // Darkest blue
            Color(0xFF110524), // Dark purple
            Color(0xFF3B063A), // Purple
            Color(0xFF691749), // Magenta dark
            Color(0xFF9C3247), // Red dark
            Color(0xFFD46453), // Orange red
            Color(0xFFF5A15D), // Orange
            Color(0xFFFFD872), // Yellow
            Color(0xFFD6FF7F), // Light green
            Color(0xFF88E060), // Green
            Color(0xFF3F9E4D), // Green dark
            Color(0xFF205648), // Teal dark
            Color(0xFF1E3A4C), // Blue dark
            Color(0xFF432565), // Purple blue
            Color(0xFF8E478C), // Purple mid
            Color(0xFFCD6093)  // Pink
        )
    )

    val slso8 = PixelPalette(
        name = "SLSO-8",
        colors = listOf(
            Color(0xFF0D2B45), // Deep blue
            Color(0xFF203C56), // Dark blue
            Color(0xFF544E68), // Purple gray
            Color(0xFF8D697A), // Mauve
            Color(0xFFD08159), // Orange brown
            Color(0xFFFFAA5E), // Orange
            Color(0xFFFFD4A3), // Peach
            Color(0xFFFFFFF4)  // Off white
        )
    )

    val oilPalette = PixelPalette(
        name = "Oil 6",
        colors = listOf(
            Color(0xFFFBF5EF), // Off white
            Color(0xFFF2D3AB), // Beige
            Color(0xFFC69FA5), // Dusty rose
            Color(0xFF8B6D9C), // Purple
            Color(0xFF494D7E), // Dark purple
            Color(0xFF272744), // Very dark blue
            Color(0xFF9AC4BC), // Sage green
            Color(0xFF5A9F8C)  // Teal
        )
    )

    val crimson = PixelPalette(
        name = "Crimson",
        colors = listOf(
            Color(0xFF1F0E1C), // Darkest
            Color(0xFF3A2335), // Dark purple
            Color(0xFF693C5E), // Purple
            Color(0xFFA05B7B), // Mauve
            Color(0xFFD787A4), // Pink
            Color(0xFFFFB9CE), // Light pink
            Color(0xFF8C3F5D), // Crimson dark
            Color(0xFFBA6A7E), // Crimson mid
            Color(0xFFF2A7B8), // Crimson light
            Color(0xFF4D2B32), // Brown dark
            Color(0xFF7E4C57), // Brown mid
            Color(0xFFB38184), // Brown light
            Color(0xFF2B1B27), // Purple black
            Color(0xFF4E3546), // Purple gray
            Color(0xFF87566D), // Purple light
            Color(0xFFDAA5B3)  // Pink light
        )
    )

    val matriax8 = PixelPalette(
        name = "Matriax 8",
        colors = listOf(
            Color(0xFFF0F0DC), // Cream
            Color(0xFFBAC2B4), // Light gray
            Color(0xFF747474), // Gray
            Color(0xFF505050), // Dark gray
            Color(0xFF2C2C2C), // Very dark gray
            Color(0xFFE06464), // Red
            Color(0xFFE0DC64), // Yellow
            Color(0xFF64E064)  // Green
        )
    )

    val nes = PixelPalette(
        name = "NES",
        colors = listOf(
            Color(0xFF000000), // Black
            Color(0xFFFCFCFC), // White
            Color(0xFFF8F8F8), // Light gray
            Color(0xFFBCBCBC), // Gray
            Color(0xFF7C7C7C), // Dark gray
            Color(0xFFF87858), // Light red
            Color(0xFFF8B800), // Yellow
            Color(0xFF00A800), // Green
            Color(0xFF0058F8), // Blue
            Color(0xFFD800CC), // Magenta
            Color(0xFFF83800), // Red
            Color(0xFFE40058), // Dark red
            Color(0xFF3CBCFC), // Cyan
            Color(0xFF6888FC), // Light blue
            Color(0xFFB8B8F8), // Pale blue
            Color(0xFFF8D8F8)  // Pale pink
        )
    )

    val edg16 = PixelPalette(
        name = "EDG-16",
        colors = listOf(
            Color(0xFFE4E6E1), // White
            Color(0xFFD4D2A5), // Beige
            Color(0xFF9D9171), // Tan
            Color(0xFF6C5D4B), // Brown light
            Color(0xFF473B33), // Brown dark
            Color(0xFF1E1E26), // Black
            Color(0xFF8E8473), // Gray brown
            Color(0xFF6BA38A), // Green gray
            Color(0xFF48856F), // Green dark
            Color(0xFF325E52), // Teal dark
            Color(0xFF8BB8A5), // Green light
            Color(0xFFB5D6C1), // Pale green
            Color(0xFFE09C63), // Orange
            Color(0xFFBC6F4B), // Brown orange
            Color(0xFF88493C), // Red brown
            Color(0xFF5C3033)  // Dark red brown
        )
    )

    val aurora = PixelPalette(
        name = "Aurora",
        colors = listOf(
            Color(0xFF011627), // Deep blue black
            Color(0xFF2E3440), // Dark gray
            Color(0xFF3B4252), // Gray
            Color(0xFF434C5E), // Light gray
            Color(0xFF4C566A), // Lighter gray
            Color(0xFFD8DEE9), // White gray
            Color(0xFFE5E9F0), // Off white
            Color(0xFFECEFF4), // White
            Color(0xFFBF616A), // Red
            Color(0xFFD08770), // Orange
            Color(0xFFEBCB8B), // Yellow
            Color(0xFFA3BE8C), // Green
            Color(0xFF88C0D0), // Cyan
            Color(0xFF81A1C1), // Blue
            Color(0xFF5E81AC), // Dark blue
            Color(0xFFB48EAD)  // Purple
        )
    )

    // List of all available palettes
    val allPalettes = listOf(
        gameboy,
        neon,
        sunset,
        ocean,
        forest,
        candy,
        cyberpunk,
        retro8bit,
        rainbow16,
        pastel16,
        earth16,
        endesga32,
        pico8,
        lospec500,
        vinik24,
        sweetie16,
        journeyPalette,
        slso8,
        oilPalette,
        crimson,
        matriax8,
        nes,
        edg16,
        aurora
    )

    fun getTodaysPalette(): PixelPalette {
        val today = java.time.LocalDate.now()
        val dateSeed = today.year * 10000 + today.monthValue * 100 + today.dayOfMonth
        // Use date seed with offset for palette to ensure variation from prompt/grid
        val random = kotlin.random.Random((dateSeed + 54321).toLong())
        return allPalettes[random.nextInt(allPalettes.size)]
    }

    fun getPaletteForDate(date: java.time.LocalDate): PixelPalette {
        val dateSeed = date.year * 10000 + date.monthValue * 100 + date.dayOfMonth
        // Use date seed with offset for palette to ensure variation from prompt/grid
        val random = kotlin.random.Random((dateSeed + 54321).toLong())
        return allPalettes[random.nextInt(allPalettes.size)]
    }
}
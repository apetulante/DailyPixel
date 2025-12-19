package com.example.dailypixel

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusRequester
import java.time.LocalDate
import java.util.UUID
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailypixel.ui.theme.DailyPixelTheme

enum class TabScreen(val title: String, val iconRes: Int) {
    Today("Today", R.drawable.ic_daily),
    Gallery("Gallery", R.drawable.ic_gallery),
    FreeDraw("Canvas", R.drawable.ic_freedraw)
}

class MainActivity : ComponentActivity() {
    private fun shareArtworkWithDate(grid: Array<Array<Color>>, title: String, date: LocalDate, isCanvasArt: Boolean = false) {
        shareArtworkInternal(this, grid, title, date, isCanvasArt)
    }

    private fun shareArtwork(context: ComponentActivity, grid: Array<Array<Color>>, title: String) {
        shareArtworkInternal(context, grid, title, LocalDate.now(), isCanvasArt = true)
    }

    private fun shareArtworkInternal(context: ComponentActivity, grid: Array<Array<Color>>, title: String, date: LocalDate, isCanvasArt: Boolean = false) {
        try {
            android.util.Log.d("ShareArtwork", "Starting share process for: $title")
            // Create bitmap from grid
            val pixelSize = 40 // Size of each pixel in the bitmap
            val bitmap = Bitmap.createBitmap(
                grid.size * pixelSize,
                grid.size * pixelSize,
                Bitmap.Config.ARGB_8888
            )
            val canvas = AndroidCanvas(bitmap)
            val paint = Paint()

            // Draw the pixel art
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    paint.color = grid[row][col].toArgb()
                    canvas.drawRect(
                        col * pixelSize.toFloat(),
                        row * pixelSize.toFloat(),
                        (col + 1) * pixelSize.toFloat(),
                        (row + 1) * pixelSize.toFloat(),
                        paint
                    )
                }
            }

            // Save bitmap to cache directory
            val file = File(context.cacheDir, "shared_artwork.png")
            android.util.Log.d("ShareArtwork", "Saving to: ${file.absolutePath}")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            android.util.Log.d("ShareArtwork", "File saved successfully, size: ${file.length()} bytes")

            // Create share intent
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            android.util.Log.d("ShareArtwork", "FileProvider URI: $uri")
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "*/*" // Allow apps to handle both image and text
                putExtra(Intent.EXTRA_STREAM, uri)
                if (isCanvasArt) {
                    putExtra(Intent.EXTRA_TEXT, "Check out this art I made with DailyPixel!")
                    putExtra(Intent.EXTRA_SUBJECT, "My DailyPixel Art")
                } else {
                    val dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
                    putExtra(Intent.EXTRA_TEXT, "Check out my DailyPixel art from $dateStr! 🎨✨\nDaily Challenge: \"$title\"\nEveryone gets the same challenge each day - see what others created! #DailyPixel")
                    putExtra(Intent.EXTRA_SUBJECT, "My DailyPixel Art - $title")
                }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            android.util.Log.d("ShareArtwork", "Starting share chooser")
            context.startActivity(Intent.createChooser(intent, "Share your pixel art"))
        } catch (e: Exception) {
            android.util.Log.e("ShareArtwork", "Error during share process", e)
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize persistent storage
        ArtStorage.initialize(this)
        DrawingStateManager.initialize(this)
        DailyPrompts.initialize(this)

        setContent {
            DailyPixelTheme {
                DailyPixelApp(
                    shareArtwork = { grid, title -> shareArtwork(this@MainActivity, grid, title) },
                    shareArtworkWithDate = { grid, title, date, isCanvasArt -> shareArtworkWithDate(grid, title, date, isCanvasArt) }
                )
            }
        }
    }
}

@Composable
fun DailyPixelApp(
    shareArtwork: (Array<Array<Color>>, String) -> Unit,
    shareArtworkWithDate: (Array<Array<Color>>, String, LocalDate, Boolean) -> Unit
) {
    var currentTab by remember { mutableStateOf(TabScreen.Today) }

    // Check for date changes
    LaunchedEffect(Unit) {
        DrawingStateManager.checkAndUpdateDate()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF3A231A), // Dark brown from your palette
                contentColor = Color.White
            ) {
                TabScreen.entries.forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Image(
                                painter = painterResource(id = tab.iconRes),
                                contentDescription = tab.title,
                                modifier = Modifier.size(28.dp) // Slightly larger icons
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = 14.sp // Bigger text
                                )
                            )
                        },
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentTab) {
            TabScreen.Today -> TodayScreen(
                shareArtwork = shareArtwork,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 40.dp)
            )
            TabScreen.Gallery -> GalleryScreen(
                onGoAgain = { savedArt ->
                    DrawingStateManager.loadFromSavedArt(savedArt)
                    currentTab = TabScreen.FreeDraw
                },
                onShare = { savedArt ->
                    val title = savedArt.title ?: savedArt.prompt
                    val isCanvasArt = savedArt.artworkType == ArtworkType.FREE_DRAW
                    shareArtworkWithDate(savedArt.pixelData, title, savedArt.date, isCanvasArt)
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 40.dp)
            )
            TabScreen.FreeDraw -> FreeDrawScreen(
                shareArtwork = shareArtwork,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 40.dp)
            )
        }
    }
}

@Composable
fun TodayScreen(shareArtwork: (Array<Array<Color>>, String) -> Unit, modifier: Modifier = Modifier) {
    val todaysPrompt = DailyPrompts.getTodaysPrompt()
    val todaysDate = LocalDate.now()
    var showEraseDialog by remember { mutableStateOf(false) }


    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title bar with erase button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Today's Challenge",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = todaysDate.format(java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            IconButton(onClick = { showEraseDialog = true }) {
                Image(
                    painter = painterResource(id = R.drawable.trash),
                    contentDescription = "Start Over",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Draw: $todaysPrompt",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TodayPixelArtCanvas(
            prompt = todaysPrompt,
            date = todaysDate,
            shareArtwork = shareArtwork
        )

        // Erase confirmation dialog
        if (showEraseDialog) {
            AlertDialog(
                onDismissRequest = { showEraseDialog = false },
                title = { Text("Erase All Work?") },
                text = { Text("This will erase all work and start over. Are you sure?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            DrawingStateManager.clearTodayCanvas()
                            showEraseDialog = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEraseDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

enum class GalleryTab(val title: String) {
    ALL("All"),
    DAILY("Daily"),
    FREE_DRAW("Free Draw")
}

@Composable
fun GalleryScreen(
    onGoAgain: (SavedPixelArt) -> Unit,
    onShare: (SavedPixelArt) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(GalleryTab.ALL) }
    var selectedArt by remember { mutableStateOf<SavedPixelArt?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }

    val savedArt = remember(selectedTab, refreshKey) {
        when (selectedTab) {
            GalleryTab.ALL -> ArtStorage.getAllSavedArt()
            GalleryTab.DAILY -> ArtStorage.getDailyArt()
            GalleryTab.FREE_DRAW -> ArtStorage.getFreeDrawArt()
        }
    }

    if (selectedArt != null) {
        // Show detailed view of selected art
        ArtDetailView(
            art = selectedArt!!,
            onBack = { selectedArt = null },
            onGoAgain = {
                onGoAgain(selectedArt!!)
                selectedArt = null
            },
            onShare = {
                onShare(selectedArt!!)
            },
            onDelete = {
                ArtStorage.deleteArt(selectedArt!!.id)
                selectedArt = null
                refreshKey++ // Trigger recomposition to refresh the gallery list
            },
            modifier = modifier
        )
    } else {
        // Show gallery list
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = "Gallery",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Tab bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GalleryTab.entries.forEach { tab ->
                    Button(
                        onClick = { selectedTab = tab },
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == tab)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface,
                            contentColor = if (selectedTab == tab)
                                MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(tab.title)
                    }
                }
            }

            if (savedArt.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.turtle_mascot),
                        contentDescription = "Turtle Mascot",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 24.dp)
                    )
                    Text(
                        text = "No saved art yet",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = "Complete today's challenge to see your first piece!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(savedArt) { _, art ->
                        GalleryItem(
                            art = art,
                            onClick = { selectedArt = art }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryItem(
    art: SavedPixelArt,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                color = MaterialTheme.colorScheme.surface, // Same background for all entries
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (art.artworkType == ArtworkType.DAILY) 2.dp else 1.dp,
                color = if (art.artworkType == ArtworkType.DAILY)
                    Color(0xFFFFE08A) // Custom yellow border for daily
                else MaterialTheme.colorScheme.outline, // Standard outline for free draw
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Art preview thumbnail
        LazyVerticalGrid(
            columns = GridCells.Fixed(art.gridSize),
            modifier = Modifier
                .size(80.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            itemsIndexed(art.pixelData.flatMapIndexed { rowIndex, row ->
                row.mapIndexed { colIndex, color ->
                    Triple(rowIndex, colIndex, color)
                }
            }) { _, item ->
                val (_, _, color) = item
                Box(
                    modifier = Modifier
                        .size(80.dp / art.gridSize)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (art.artworkType == ArtworkType.FREE_DRAW)
                        art.title ?: "Untitled"
                    else art.getDisplayDate(),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = if (art.artworkType == ArtworkType.DAILY)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = if (art.artworkType == ArtworkType.DAILY)
                                R.drawable.ic_daily
                            else R.drawable.ic_freedraw
                        ),
                        contentDescription = if (art.artworkType == ArtworkType.DAILY) "Daily" else "Free Draw",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                text = if (art.artworkType == ArtworkType.FREE_DRAW)
                    art.getDisplayDate()
                else art.prompt,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = if (art.artworkType == ArtworkType.FREE_DRAW)
                    "${art.gridSize}x${art.gridSize}"
                else "Palette: ${art.paletteName} • ${art.gridSize}x${art.gridSize}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Text(
            text = "→",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ArtDetailView(
    art: SavedPixelArt,
    onBack: () -> Unit,
    onGoAgain: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title bar with back and delete buttons - consistent with other screens
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (art.artworkType == ArtworkType.FREE_DRAW && art.title != null)
                        art.title!!
                    else art.prompt,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = art.getDisplayDate(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (art.artworkType == ArtworkType.DAILY) {
                    Text(
                        text = "Palette: ${art.paletteName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                } else {
                    Text(
                        text = "Free Draw",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Image(
                    painter = painterResource(id = R.drawable.trash),
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the pixel art
        LazyVerticalGrid(
            columns = GridCells.Fixed(art.gridSize),
            modifier = Modifier
                .size(320.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            itemsIndexed(art.pixelData.flatMapIndexed { rowIndex, row ->
                row.mapIndexed { colIndex, color ->
                    Triple(rowIndex, colIndex, color)
                }
            }) { _, item ->
                val (_, _, color) = item
                Box(
                    modifier = Modifier
                        .size(320.dp / art.gridSize)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onShare
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.share),
                    contentDescription = "Share",
                    modifier = Modifier.size(20.dp)
                )
                Text("Share")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onGoAgain
        ) {
            Text(
                if (art.artworkType == ArtworkType.FREE_DRAW) "Edit 🎨"
                else "Go Again! 🎨"
            )
        }

        // Delete confirmation dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Artwork?") },
                text = { Text("Permanently delete this artwork? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDelete()
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun FreeDrawScreen(shareArtwork: (Array<Array<Color>>, String) -> Unit, modifier: Modifier = Modifier) {
    var showEraseDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title bar with erase button - consistent with TodayScreen
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (DrawingStateManager.editingArtworkId != null)
                        "Editing: ${DrawingStateManager.editingArtworkTitle.ifBlank { "Untitled" }}"
                    else "Canvas Mode",
                    style = MaterialTheme.typography.headlineLarge
                )
                if (DrawingStateManager.editingArtworkId != null) {
                    Text(
                        text = "🎨 Editing Mode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            IconButton(onClick = { showEraseDialog = true }) {
                Image(
                    painter = painterResource(id = R.drawable.trash),
                    contentDescription = "Start Over",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (DrawingStateManager.editingArtworkId != null) {
            Button(
                onClick = { DrawingStateManager.clearFreeDrawCanvas() },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("New Drawing")
            }
        }

        PixelArtScreen(shareArtwork = shareArtwork, modifier = Modifier)
    }

    // Erase dialog - consistent with TodayScreen
    if (showEraseDialog) {
        AlertDialog(
            onDismissRequest = { showEraseDialog = false },
            title = { Text("Start Over?") },
            text = { Text("This will clear your current drawing. Are you sure?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        DrawingStateManager.clearFreeDrawCanvas()
                        showEraseDialog = false
                    }
                ) {
                    Text("Yes, Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEraseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TodayPixelArtCanvas(
    prompt: String,
    date: LocalDate,
    shareArtwork: (Array<Array<Color>>, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val todaysPalette = ColorPalettes.getTodaysPalette()
    val focusRequester = remember { FocusRequester() }
    var showSaveSuccessDialog by remember { mutableStateOf(false) }
    var showOverwriteDialog by remember { mutableStateOf(false) }
    var existingSavedArt by remember { mutableStateOf<SavedPixelArt?>(null) }


    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Show today's assigned palette and grid size
        Text(
            text = "Today's Palette: ${todaysPalette.name}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Grid Size: ${DrawingStateManager.todayGridSize}x${DrawingStateManager.todayGridSize}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ColorPalette(
            colors = todaysPalette.colors,
            selectedColor = DrawingStateManager.todaySelectedColor,
            onColorSelected = { DrawingStateManager.todaySelectedColor = it },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Grid")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = DrawingStateManager.todayShowGrid,
                    onCheckedChange = { DrawingStateManager.todayShowGrid = it }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Paint")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = DrawingStateManager.todayFreeDrawMode,
                    onCheckedChange = { DrawingStateManager.todayFreeDrawMode = it }
                )
            }
        }

        if (!DrawingStateManager.todayFreeDrawMode) {
            Text(
                text = "Cursor Mode: Drag to move, tap to draw",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        } else {
            Text(
                text = "Paint Mode: Drag to draw directly",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        PixelGrid(
            grid = DrawingStateManager.todayGrid,
            selectedColor = DrawingStateManager.todaySelectedColor,
            showGrid = DrawingStateManager.todayShowGrid,
            cursorX = DrawingStateManager.todayCursorX,
            cursorY = DrawingStateManager.todayCursorY,
            freeDrawMode = DrawingStateManager.todayFreeDrawMode,
            onPixelClick = { row, col ->
                DrawingStateManager.drawPixelToday(row, col, DrawingStateManager.todaySelectedColor)
            },
            onCursorMove = { x, y ->
                DrawingStateManager.todayCursorX = x.coerceIn(0, DrawingStateManager.todayGridSize - 1)
                DrawingStateManager.todayCursorY = y.coerceIn(0, DrawingStateManager.todayGridSize - 1)
            },
            onTapToDraw = {
                DrawingStateManager.drawPixelToday(
                    DrawingStateManager.todayCursorY,
                    DrawingStateManager.todayCursorX,
                    DrawingStateManager.todaySelectedColor
                )
                DrawingStateManager.finishDrawingBatchToday()
            },
            isToday = true,
            modifier = Modifier.focusRequester(focusRequester)
                .onKeyEvent { keyEvent ->
                    if (!DrawingStateManager.todayFreeDrawMode && keyEvent.type == KeyEventType.KeyDown) {
                        when (keyEvent.key) {
                            Key.DirectionUp -> {
                                DrawingStateManager.todayCursorY = (DrawingStateManager.todayCursorY - 1).coerceIn(0, DrawingStateManager.todayGridSize - 1)
                                true
                            }
                            Key.DirectionDown -> {
                                DrawingStateManager.todayCursorY = (DrawingStateManager.todayCursorY + 1).coerceIn(0, DrawingStateManager.todayGridSize - 1)
                                true
                            }
                            Key.DirectionLeft -> {
                                DrawingStateManager.todayCursorX = (DrawingStateManager.todayCursorX - 1).coerceIn(0, DrawingStateManager.todayGridSize - 1)
                                true
                            }
                            Key.DirectionRight -> {
                                DrawingStateManager.todayCursorX = (DrawingStateManager.todayCursorX + 1).coerceIn(0, DrawingStateManager.todayGridSize - 1)
                                true
                            }
                            Key.Spacebar, Key.Enter -> {
                                DrawingStateManager.todayGrid = DrawingStateManager.todayGrid.copyOf().apply {
                                    this[DrawingStateManager.todayCursorY] = this[DrawingStateManager.todayCursorY].copyOf().apply {
                                        this[DrawingStateManager.todayCursorX] = DrawingStateManager.todaySelectedColor
                                    }
                                }
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Undo button
            Button(
                onClick = { DrawingStateManager.undoToday() },
                modifier = Modifier.weight(1f),
                enabled = DrawingStateManager.canUndoToday
            ) {
                Image(
                    painter = painterResource(id = R.drawable.undo),
                    contentDescription = "Undo",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Save button
            Button(
                onClick = {
                    val existing = ArtStorage.getArtByDate(date)
                    if (existing != null) {
                        existingSavedArt = existing
                        showOverwriteDialog = true
                    } else {
                        val savedArt = SavedPixelArt(
                            id = UUID.randomUUID().toString(),
                            date = date,
                            prompt = prompt,
                            paletteName = todaysPalette.name,
                            gridSize = DrawingStateManager.todayGridSize,
                            pixelData = DrawingStateManager.todayGrid
                        )
                        ArtStorage.saveArt(savedArt)
                        DrawingStateManager.markTodaySaved()
                        showSaveSuccessDialog = true
                    }
                },
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    if (ArtStorage.getArtByDate(date) != null)
                        "Update Today's Art"
                    else
                        "Save Today's Art"
                )
            }

            // Redo button
            Button(
                onClick = { DrawingStateManager.redoToday() },
                modifier = Modifier.weight(1f),
                enabled = DrawingStateManager.canRedoToday
            ) {
                Image(
                    painter = painterResource(id = R.drawable.redo),
                    contentDescription = "Redo",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Save success dialog
        if (showSaveSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSaveSuccessDialog = false },
                title = { Text("🎨 Daily Art Saved!") },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.palette),
                            contentDescription = "Saved Artwork",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(bottom = 16.dp)
                        )
                        Text("Your pixel art has been saved to the gallery!")
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                shareArtwork(DrawingStateManager.todayGrid, prompt)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.share),
                                    contentDescription = "Share",
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Share")
                            }
                        }
                        TextButton(onClick = { showSaveSuccessDialog = false }) {
                            Text("OK")
                        }
                    }
                }
            )
        }

        // Overwrite confirmation dialog
        if (showOverwriteDialog && existingSavedArt != null) {
            AlertDialog(
                onDismissRequest = { showOverwriteDialog = false },
                title = { Text("Overwrite Previous Save?") },
                text = {
                    Column {
                        Text(
                            text = "You already have art saved for today. Do you want to replace it?",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Previous save:",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        // Show preview of existing saved art (centered)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(existingSavedArt!!.gridSize),
                                modifier = Modifier
                                    .size(120.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
                                verticalArrangement = Arrangement.spacedBy(0.dp),
                                horizontalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                itemsIndexed(existingSavedArt!!.pixelData.flatMapIndexed { rowIndex, row ->
                                    row.mapIndexed { colIndex, color ->
                                        Triple(rowIndex, colIndex, color)
                                    }
                                }) { _, item ->
                                    val (_, _, color) = item
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp / existingSavedArt!!.gridSize)
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val savedArt = SavedPixelArt(
                                id = UUID.randomUUID().toString(),
                                date = date,
                                prompt = prompt,
                                paletteName = todaysPalette.name,
                                gridSize = DrawingStateManager.todayGridSize,
                                pixelData = DrawingStateManager.todayGrid
                            )
                            ArtStorage.saveArt(savedArt)
                            DrawingStateManager.markTodaySaved()
                            showOverwriteDialog = false
                            showSaveSuccessDialog = true
                        }
                    ) {
                        Text("Yes, Replace")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showOverwriteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixelArtScreen(shareArtwork: (Array<Array<Color>>, String) -> Unit, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSaveSuccessDialog by remember { mutableStateOf(false) }
    var showPaletteBottomSheet by remember { mutableStateOf(false) }
    var showGridSizeBottomSheet by remember { mutableStateOf(false) }
    var showGridResizeWarning by remember { mutableStateOf(false) }
    var pendingGridSize by remember { mutableIntStateOf(8) }
    var artworkTitle by remember { mutableStateOf("") }

    // Initialize from editing state
    LaunchedEffect(Unit) {
        if (DrawingStateManager.editingArtworkId != null) {
            artworkTitle = DrawingStateManager.editingArtworkTitle
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                if (!DrawingStateManager.freeDrawFreeDrawMode && keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionUp -> {
                            DrawingStateManager.freeDrawCursorY = (DrawingStateManager.freeDrawCursorY - 1).coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
                            true
                        }
                        Key.DirectionDown -> {
                            DrawingStateManager.freeDrawCursorY = (DrawingStateManager.freeDrawCursorY + 1).coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
                            true
                        }
                        Key.DirectionLeft -> {
                            DrawingStateManager.freeDrawCursorX = (DrawingStateManager.freeDrawCursorX - 1).coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
                            true
                        }
                        Key.DirectionRight -> {
                            DrawingStateManager.freeDrawCursorX = (DrawingStateManager.freeDrawCursorX + 1).coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
                            true
                        }
                        Key.Spacebar, Key.Enter -> {
                            DrawingStateManager.freeDrawGrid = DrawingStateManager.freeDrawGrid.copyOf().apply {
                                this[DrawingStateManager.freeDrawCursorY] = this[DrawingStateManager.freeDrawCursorY].copyOf().apply {
                                    this[DrawingStateManager.freeDrawCursorX] = DrawingStateManager.freeDrawSelectedColor
                                }
                            }
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // Color Palette and Grid Size Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showPaletteBottomSheet = true },
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.palette),
                        contentDescription = "Palette",
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Colors")
                }
            }
            Button(
                onClick = { showGridSizeBottomSheet = true },
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.grid),
                        contentDescription = "Grid",
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Grid")
                }
            }
        }

        ColorPalette(
            colors = DrawingStateManager.freeDrawCurrentPalette.colors,
            selectedColor = DrawingStateManager.freeDrawSelectedColor,
            onColorSelected = { DrawingStateManager.freeDrawSelectedColor = it },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Grid")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = DrawingStateManager.freeDrawShowGrid,
                    onCheckedChange = { DrawingStateManager.freeDrawShowGrid = it }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Paint")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = DrawingStateManager.freeDrawFreeDrawMode,
                    onCheckedChange = { DrawingStateManager.freeDrawFreeDrawMode = it }
                )
            }
        }

        if (!DrawingStateManager.freeDrawFreeDrawMode) {
            Text(
                text = "Cursor Mode: Drag to move, tap to draw",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        } else {
            Text(
                text = "Paint Mode: Drag to draw directly",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        PixelGrid(
            grid = DrawingStateManager.freeDrawGrid,
            selectedColor = DrawingStateManager.freeDrawSelectedColor,
            showGrid = DrawingStateManager.freeDrawShowGrid,
            cursorX = DrawingStateManager.freeDrawCursorX,
            cursorY = DrawingStateManager.freeDrawCursorY,
            freeDrawMode = DrawingStateManager.freeDrawFreeDrawMode,
            onPixelClick = { row, col ->
                DrawingStateManager.drawPixelFreeMode(row, col, DrawingStateManager.freeDrawSelectedColor)
            },
            onCursorMove = { x, y ->
                DrawingStateManager.freeDrawCursorX = x.coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
                DrawingStateManager.freeDrawCursorY = y.coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
            },
            onTapToDraw = {
                DrawingStateManager.drawPixelFreeMode(
                    DrawingStateManager.freeDrawCursorY,
                    DrawingStateManager.freeDrawCursorX,
                    DrawingStateManager.freeDrawSelectedColor
                )
                DrawingStateManager.finishDrawingBatchFreeMode()
            },
            modifier = Modifier.focusRequester(focusRequester)
                .onKeyEvent { keyEvent ->
                    if (!DrawingStateManager.freeDrawFreeDrawMode && keyEvent.type == KeyEventType.KeyDown) {
                        when (keyEvent.key) {
                            Key.DirectionUp -> {
                                DrawingStateManager.freeDrawCursorY = (DrawingStateManager.freeDrawCursorY - 1).coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
                                true
                            }
                            Key.DirectionDown -> {
                                DrawingStateManager.freeDrawCursorY = (DrawingStateManager.freeDrawCursorY + 1).coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
                                true
                            }
                            Key.DirectionLeft -> {
                                DrawingStateManager.freeDrawCursorX = (DrawingStateManager.freeDrawCursorX - 1).coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
                                true
                            }
                            Key.DirectionRight -> {
                                DrawingStateManager.freeDrawCursorX = (DrawingStateManager.freeDrawCursorX + 1).coerceIn(0, DrawingStateManager.freeDrawGridSize - 1)
                                true
                            }
                            Key.Spacebar, Key.Enter -> {
                                DrawingStateManager.freeDrawGrid = DrawingStateManager.freeDrawGrid.copyOf().apply {
                                    this[DrawingStateManager.freeDrawCursorY] = this[DrawingStateManager.freeDrawCursorY].copyOf().apply {
                                        this[DrawingStateManager.freeDrawCursorX] = DrawingStateManager.freeDrawSelectedColor
                                    }
                                }
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Undo button
            Button(
                onClick = { DrawingStateManager.undoFreeMode() },
                modifier = Modifier.weight(1f),
                enabled = DrawingStateManager.canUndoFreeMode
            ) {
                Image(
                    painter = painterResource(id = R.drawable.undo),
                    contentDescription = "Undo",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Save button
            Button(
                onClick = { showSaveDialog = true },
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    if (DrawingStateManager.editingArtworkId != null) "Save Changes"
                    else "Save Canvas Art"
                )
            }

            // Redo button
            Button(
                onClick = { DrawingStateManager.redoFreeMode() },
                modifier = Modifier.weight(1f),
                enabled = DrawingStateManager.canRedoFreeMode
            ) {
                Image(
                    painter = painterResource(id = R.drawable.redo),
                    contentDescription = "Redo",
                    modifier = Modifier.size(20.dp)
                )
            }
        }


        // Save dialog - different behavior for editing vs new
        if (showSaveDialog) {
            if (DrawingStateManager.editingArtworkId != null) {
                // Editing existing artwork - show overwrite/save as new options
                AlertDialog(
                    onDismissRequest = { showSaveDialog = false },
                    title = { Text("Save Changes") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = artworkTitle,
                                onValueChange = { artworkTitle = it },
                                label = { Text("Title") },
                                placeholder = { Text("Untitled") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            )
                            Text("How do you want to save your changes?")
                        }
                    },
                    confirmButton = {
                        Column {
                            TextButton(
                                onClick = {
                                    // Overwrite existing
                                    val savedArt = SavedPixelArt(
                                        id = DrawingStateManager.editingArtworkId!!,
                                        date = java.time.LocalDate.now(),
                                        prompt = artworkTitle.ifBlank { "Untitled" },
                                        paletteName = DrawingStateManager.freeDrawCurrentPalette.name,
                                        gridSize = DrawingStateManager.freeDrawGridSize,
                                        pixelData = DrawingStateManager.freeDrawGrid,
                                        artworkType = ArtworkType.FREE_DRAW,
                                        title = artworkTitle.ifBlank { null }
                                    )
                                    ArtStorage.saveArt(savedArt)
                                    DrawingStateManager.editingArtworkTitle = artworkTitle
                                    showSaveDialog = false
                                    showSaveSuccessDialog = true
                                }
                            ) {
                                Text("Overwrite Original")
                            }
                            TextButton(
                                onClick = {
                                    // Save as new
                                    val savedArt = SavedPixelArt(
                                        id = java.util.UUID.randomUUID().toString(),
                                        date = java.time.LocalDate.now(),
                                        prompt = artworkTitle.ifBlank { "Untitled" },
                                        paletteName = DrawingStateManager.freeDrawCurrentPalette.name,
                                        gridSize = DrawingStateManager.freeDrawGridSize,
                                        pixelData = DrawingStateManager.freeDrawGrid,
                                        artworkType = ArtworkType.FREE_DRAW,
                                        title = artworkTitle.ifBlank { null }
                                    )
                                    ArtStorage.saveArt(savedArt)
                                    DrawingStateManager.editingArtworkId = savedArt.id
                                    DrawingStateManager.editingArtworkTitle = artworkTitle
                                    showSaveDialog = false
                                    showSaveSuccessDialog = true
                                }
                            ) {
                                Text("Save as New")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSaveDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            } else {
                // New artwork
                AlertDialog(
                    onDismissRequest = { showSaveDialog = false },
                    title = { Text("Save Canvas Art") },
                    text = {
                        Column {
                            Text(
                                text = "Give your artwork a title (optional):",
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = artworkTitle,
                                onValueChange = { artworkTitle = it },
                                placeholder = { Text("Untitled") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val savedArt = SavedPixelArt(
                                    id = java.util.UUID.randomUUID().toString(),
                                    date = java.time.LocalDate.now(),
                                    prompt = artworkTitle.ifBlank { "Untitled" },
                                    paletteName = DrawingStateManager.freeDrawCurrentPalette.name,
                                    gridSize = DrawingStateManager.freeDrawGridSize,
                                    pixelData = DrawingStateManager.freeDrawGrid,
                                    artworkType = ArtworkType.FREE_DRAW,
                                    title = artworkTitle.ifBlank { null }
                                )
                                ArtStorage.saveArt(savedArt)
                                DrawingStateManager.editingArtworkId = savedArt.id
                                DrawingStateManager.editingArtworkTitle = artworkTitle
                                showSaveDialog = false
                                showSaveSuccessDialog = true
                            }
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSaveDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        // Save success dialog
        if (showSaveSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSaveSuccessDialog = false },
                title = { Text("🎨 Canvas Art Saved!") },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.palette),
                            contentDescription = "Saved Artwork",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(bottom = 16.dp)
                        )
                        Text("Your artwork has been saved to the gallery!")
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                val title = if (DrawingStateManager.editingArtworkTitle.isNotBlank())
                                    DrawingStateManager.editingArtworkTitle
                                else "Untitled"
                                shareArtwork(DrawingStateManager.freeDrawGrid, title)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.share),
                                    contentDescription = "Share",
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Share")
                            }
                        }
                        TextButton(onClick = { showSaveSuccessDialog = false }) {
                            Text("OK")
                        }
                    }
                }
            )
        }


        // Palette Selection Bottom Sheet
        if (showPaletteBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPaletteBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Choose Color Palette",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 8-Color Palettes section
                        val eightColorPalettes = ColorPalettes.allPalettes.filter { it.colors.size <= 8 }
                        if (eightColorPalettes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "8-Color Palettes",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                )
                            }
                            items(eightColorPalettes) { palette ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        DrawingStateManager.freeDrawCurrentPalette = palette
                                        DrawingStateManager.freeDrawSelectedColor = palette.colors[0]
                                        showPaletteBottomSheet = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (palette == DrawingStateManager.freeDrawCurrentPalette)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = palette.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )

                                    // Show colors in rows - two rows for 16+ colors, one row for 8 or fewer
                                    if (palette.colors.size > 8) {
                                        // Two rows for 16-color palettes
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                palette.colors.take(8).forEach { color ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .background(color, CircleShape)
                                                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                                    )
                                                }
                                            }
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                palette.colors.drop(8).forEach { color ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .background(color, CircleShape)
                                                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Single row for 8-color palettes
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            palette.colors.forEach { color ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(color, CircleShape)
                                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            }
                        }

                        // 16-Color Palettes section
                        val sixteenColorPalettes = ColorPalettes.allPalettes.filter { it.colors.size > 8 }
                        if (sixteenColorPalettes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "16-Color Palettes",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }
                            items(sixteenColorPalettes) { palette ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        DrawingStateManager.freeDrawCurrentPalette = palette
                                        DrawingStateManager.freeDrawSelectedColor = palette.colors[0]
                                        showPaletteBottomSheet = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (palette == DrawingStateManager.freeDrawCurrentPalette)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = palette.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )

                                    // Two rows for 16-color palettes
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            palette.colors.take(8).forEach { color ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(color, CircleShape)
                                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                                )
                                            }
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            palette.colors.drop(8).forEach { color ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(color, CircleShape)
                                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Grid Size Bottom Sheet
        if (showGridSizeBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGridSizeBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Choose Grid Size",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val gridSizeOptions = listOf(8, 16, 32)

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(gridSizeOptions) { size ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        pendingGridSize = size
                                        if (DrawingStateManager.wouldGridResizeLosePixels(size)) {
                                            showGridResizeWarning = true
                                        } else {
                                            DrawingStateManager.resizeFreeDrawGrid(size)
                                            showGridSizeBottomSheet = false
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (size == DrawingStateManager.freeDrawGridSize)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "${size}×${size} Grid",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = when (size) {
                                                8 -> "Small - Quick sketches"
                                                16 -> "Medium - Detailed art"
                                                32 -> "Large - Complex designs"
                                                else -> "Grid size $size"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }

                                    if (size == DrawingStateManager.freeDrawGridSize) {
                                        Text(
                                            text = "Current",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Grid Resize Warning Dialog
        if (showGridResizeWarning) {
            AlertDialog(
                onDismissRequest = { showGridResizeWarning = false },
                title = { Text("Warning: Data Loss") },
                text = {
                    Text("Decreasing grid size will delete portions of the current drawing that extend beyond the new grid boundaries. This cannot be undone. Proceed?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            DrawingStateManager.resizeFreeDrawGrid(pendingGridSize)
                            showGridResizeWarning = false
                            showGridSizeBottomSheet = false
                        }
                    ) {
                        Text("Yes, Resize")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showGridResizeWarning = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


@Composable
fun ColorPalette(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        // Calculate available space and determine sizing
        val totalItems = colors.size + 1 // +1 for eraser
        val availableWidth = maxWidth
        val minSpacing = 1.dp
        val minCircleSize = 20.dp // Reduced for better fit with many colors

        // Calculate optimal circle size based on available space
        val totalSpacingWidth = minSpacing * (totalItems - 1)
        val availableForCircles = availableWidth - totalSpacingWidth
        val optimalCircleSize = (availableForCircles / totalItems).coerceAtMost(40.dp).coerceAtLeast(minCircleSize)

        // Calculate actual spacing to center everything
        val actualSpacing = if (optimalCircleSize > minCircleSize) {
            val usedWidth = optimalCircleSize * totalItems
            val remainingWidth = availableWidth - usedWidth
            (remainingWidth / (totalItems - 1)).coerceAtLeast(minSpacing)
        } else {
            // For tight layouts, use minimal spacing
            if (totalItems > 12) 0.5.dp else minSpacing
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(actualSpacing, Alignment.CenterHorizontally)
        ) {
            // Regular color buttons
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(optimalCircleSize)
                        .background(color, CircleShape)
                        .border(
                            width = if (color == selectedColor) 3.dp else 1.dp,
                            color = if (color == selectedColor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(color) }
                )
            }

            // Eraser button (white with eraser icon)
            Box(
                modifier = Modifier
                    .size(optimalCircleSize)
                    .background(Color.White, CircleShape)
                    .border(
                        width = if (selectedColor == Color.White) 3.dp else 1.dp,
                        color = if (selectedColor == Color.White) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(Color.White) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.eraser),
                    contentDescription = "Eraser",
                    modifier = Modifier.size((optimalCircleSize * 0.5f).coerceAtMost(20.dp).coerceAtLeast(12.dp))
                )
            }
        }
    }
}

@Composable
fun PixelGrid(
    grid: Array<Array<Color>>,
    selectedColor: Color,
    showGrid: Boolean,
    cursorX: Int,
    cursorY: Int,
    freeDrawMode: Boolean,
    onPixelClick: (Int, Int) -> Unit,
    onCursorMove: (Int, Int) -> Unit,
    onTapToDraw: () -> Unit,
    isToday: Boolean = false,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val gridLength = grid.size
        val maxAvailableSize = minOf(maxWidth, maxHeight * 0.8f) // Use available space but leave room for UI
        val maxGridSize = maxAvailableSize.coerceAtMost(400.dp) // Reasonable maximum
        val pixelSize = maxGridSize / gridLength // Dynamic pixel size based on grid length
        val gridSize = maxGridSize

        Box(
            modifier = Modifier
                .size(gridSize)
                .align(Alignment.Center)
            .let {
                if (showGrid) it.border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                else it
            }
            .pointerInput(freeDrawMode, gridLength) {
                if (freeDrawMode) {
                    var lastDrawnX = -1
                    var lastDrawnY = -1

                    detectDragGestures(
                        onDragStart = { offset ->
                            val x = (offset.x / (size.width / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)
                            val y = (offset.y / (size.height / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)
                            lastDrawnX = x
                            lastDrawnY = y
                            onPixelClick(y, x)
                        },
                        onDragEnd = {
                            // Finish the drawing batch for free draw mode
                            if (freeDrawMode) {
                                if (isToday) {
                                    DrawingStateManager.finishDrawingBatchToday()
                                } else {
                                    DrawingStateManager.finishDrawingBatchFreeMode()
                                }
                            }
                            // Reset tracking
                            lastDrawnX = -1
                            lastDrawnY = -1
                        }
                    ) { change, _ ->
                        val x = (change.position.x / (size.width / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)
                        val y = (change.position.y / (size.height / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)

                        // Only draw if we've moved to a different pixel
                        if (x != lastDrawnX || y != lastDrawnY) {
                            lastDrawnX = x
                            lastDrawnY = y
                            onPixelClick(y, x)
                        }
                    }
                } else {
                    var lastCursorX = -1
                    var lastCursorY = -1

                    detectDragGestures(
                        onDragStart = { offset ->
                            // Convert touch position to grid coordinates
                            val x = (offset.x / (size.width / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)
                            val y = (offset.y / (size.height / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)
                            lastCursorX = x
                            lastCursorY = y
                            onCursorMove(x, y)
                        },
                        onDragEnd = {
                            // Cursor mode doesn't need batch finishing since it doesn't draw during drag
                            // Reset tracking
                            lastCursorX = -1
                            lastCursorY = -1
                        }
                    ) { change, _ ->
                        // Convert drag position to grid coordinates
                        val x = (change.position.x / (size.width / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)
                        val y = (change.position.y / (size.height / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)

                        // Only move cursor if we've moved to a different pixel
                        if (x != lastCursorX || y != lastCursorY) {
                            lastCursorX = x
                            lastCursorY = y
                            onCursorMove(x, y)
                        }
                    }
                }
            }
            .pointerInput(freeDrawMode, gridLength) {
                if (!freeDrawMode) {
                    detectTapGestures {
                        onTapToDraw()
                    }
                } else {
                    detectTapGestures { offset ->
                        val x = (offset.x / (size.width / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)
                        val y = (offset.y / (size.height / gridLength.toFloat())).toInt().coerceIn(0, gridLength - 1)
                        onPixelClick(y, x)
                        // Finish batch for single tap in free draw mode
                        if (freeDrawMode) {
                            if (isToday) {
                                DrawingStateManager.finishDrawingBatchToday()
                            } else {
                                DrawingStateManager.finishDrawingBatchFreeMode()
                            }
                        }
                    }
                }
            }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridLength),
            modifier = Modifier.size(gridSize),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            userScrollEnabled = false
        ) {
            itemsIndexed(grid.flatMapIndexed { rowIndex, row ->
                row.mapIndexed { colIndex, color ->
                    Triple(rowIndex, colIndex, color)
                }
            }) { _, item ->
                val (row, col, color) = item
                val isCursor = !freeDrawMode && cursorX == col && cursorY == row

                Box(
                    modifier = Modifier
                        .size(pixelSize)
                        .background(color)
                        .let {
                            if (showGrid) it.border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            else it
                        }
                        .let {
                            if (isCursor) it.border(3.dp, MaterialTheme.colorScheme.primary)
                            else it
                        }
                )
            }
        }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyPixelAppPreview() {
    DailyPixelTheme {
        DailyPixelApp(
            shareArtwork = { _, _ -> /* Preview mock */ },
            shareArtworkWithDate = { _, _, _, _ -> /* Preview mock */ }
        )
    }
}
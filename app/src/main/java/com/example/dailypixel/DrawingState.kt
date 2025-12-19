package com.example.dailypixel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalDate

data class DrawOperation(
    val row: Int,
    val col: Int,
    val oldColor: Color,
    val newColor: Color
)

data class OperationBatch(
    val operations: List<DrawOperation>
)

object DrawingStateManager {
    private var context: Context? = null

    // Initialize with proper grid size
    private val initialGridSize = DailyPrompts.getTodaysGridSize()

    // Today's drawing state
    var todayGridSize by mutableStateOf(initialGridSize)
    var todayGrid by mutableStateOf(Array(initialGridSize) { Array(initialGridSize) { Color.White } })
    var todaySelectedColor by mutableStateOf(ColorPalettes.getTodaysPalette().colors[0])
    var todayShowGrid by mutableStateOf(true)
    var todayFreeDrawMode by mutableStateOf(false)
    var todayCursorX by mutableStateOf(0)
    var todayCursorY by mutableStateOf(0)
    var todayDate by mutableStateOf(LocalDate.now())
    var todayHasBeenSaved by mutableStateOf(false)

    // Track if we've been properly initialized
    private var isInitialized = false

    // Free draw state
    var freeDrawGridSize by mutableStateOf(8)
    var freeDrawGrid by mutableStateOf(Array(8) { Array(8) { Color.White } })
    var freeDrawSelectedColor by mutableStateOf(Color.Black)
    var freeDrawCurrentPalette by mutableStateOf(ColorPalettes.allPalettes.first())
    var freeDrawShowGrid by mutableStateOf(true)
    var freeDrawFreeDrawMode by mutableStateOf(false)
    var freeDrawCursorX by mutableStateOf(0)
    var freeDrawCursorY by mutableStateOf(0)
    var editingArtworkId by mutableStateOf<String?>(null)
    var editingArtworkTitle by mutableStateOf("")

    // Undo/Redo state for Today's drawing
    private val todayUndoHistory = mutableListOf<OperationBatch>()
    private val todayRedoHistory = mutableListOf<OperationBatch>()
    private var todayCurrentBatch = mutableListOf<DrawOperation>()

    // Undo/Redo state for Free draw
    private val freeDrawUndoHistory = mutableListOf<OperationBatch>()
    private val freeDrawRedoHistory = mutableListOf<OperationBatch>()
    private var freeDrawCurrentBatch = mutableListOf<DrawOperation>()

    fun clearTodayCanvas() {
        // Ensure we have the right grid size
        todayGridSize = DailyPrompts.getTodaysGridSize()
        todayGrid = Array(todayGridSize) { Array(todayGridSize) { Color.White } }
        todaySelectedColor = ColorPalettes.getTodaysPalette().colors[0]
        todayCursorX = 0
        todayCursorY = 0
        clearUndoRedoHistoryToday()
    }

    fun clearFreeDrawCanvas() {
        freeDrawGrid = Array(freeDrawGridSize) { Array(freeDrawGridSize) { Color.White } }
        freeDrawSelectedColor = freeDrawCurrentPalette.colors[0]
        freeDrawCursorX = 0
        freeDrawCursorY = 0
        editingArtworkId = null
        editingArtworkTitle = ""
        clearUndoRedoHistoryFreeMode()
    }

    fun loadFromSavedArt(art: SavedPixelArt) {
        // Load the saved art into free draw mode
        freeDrawGridSize = art.gridSize
        freeDrawGrid = art.pixelData.map { it.copyOf() }.toTypedArray()
        freeDrawCurrentPalette = ColorPalettes.allPalettes.find { it.name == art.paletteName }
            ?: ColorPalettes.allPalettes.first()
        freeDrawSelectedColor = freeDrawCurrentPalette.colors[0]
        freeDrawCursorX = 0
        freeDrawCursorY = 0

        // Set editing state for free draws
        if (art.artworkType == ArtworkType.FREE_DRAW) {
            editingArtworkId = art.id
            editingArtworkTitle = art.title ?: ""
        } else {
            editingArtworkId = null
            editingArtworkTitle = ""
        }
    }

    fun initializeIfNeeded() {
        if (!isInitialized) {
            // Only initialize once per app session
            val currentGridSize = DailyPrompts.getTodaysGridSize()

            // Only resize grid if it's a different size AND still has the default empty state
            if (todayGrid.size != currentGridSize && isGridEmpty()) {
                todayGridSize = currentGridSize
                todayGrid = Array(currentGridSize) { Array(currentGridSize) { Color.White } }
                clearUndoRedoHistoryToday() // Clear history since grid changed
            }

            // Only reset color if it's still the default black
            if (todaySelectedColor == Color.Black) {
                todaySelectedColor = ColorPalettes.getTodaysPalette().colors[0]
            }

            todayHasBeenSaved = ArtStorage.hasArtForToday()
            isInitialized = true
            updateUndoRedoState()
        }
    }

    private fun isGridEmpty(): Boolean {
        return todayGrid.all { row -> row.all { pixel -> pixel == Color.White } }
    }

    fun initialize(context: Context) {
        this.context = context

        // Check if this is a fresh install by looking at existing data and flag
        val prefs = context.getSharedPreferences("drawing_state", Context.MODE_PRIVATE)
        val isInitialized = prefs.getBoolean("app_initialized", false)
        val hasExistingData = prefs.getString("canvas_date", null) != null

        if (!isInitialized && !hasExistingData) {
            // True fresh install - no flag and no data
            prefs.edit().clear().apply()
            prefs.edit().putBoolean("app_initialized", true).apply()
        } else if (!isInitialized && hasExistingData) {
            // Data exists but flag missing - just set flag, don't clear data
            prefs.edit().putBoolean("app_initialized", true).apply()
        }

        loadTodayCanvasState()
    }

    private fun loadTodayCanvasState() {
        val prefs = context?.getSharedPreferences("drawing_state", Context.MODE_PRIVATE) ?: return
        val today = LocalDate.now().toString()

        val savedDate = prefs.getString("canvas_date", "")
        if (savedDate == today) {
            // Load saved canvas for today
            val gridSizeStr = prefs.getString("canvas_grid_size", null)
            val canvasData = prefs.getString("canvas_data", null)

            if (gridSizeStr != null && canvasData != null) {
                try {
                    val savedGridSize = gridSizeStr.toInt()
                    val colorInts = canvasData.split("|").map { row ->
                        row.split(",").map { it.toInt() }
                    }

                    // Check if the saved grid size is still valid (32 is no longer supported)
                    val currentValidGridSize = DailyPrompts.getTodaysGridSize()
                    if (savedGridSize == currentValidGridSize &&
                        colorInts.size == savedGridSize &&
                        colorInts.all { it.size == savedGridSize }) {
                        todayGridSize = savedGridSize
                        todayGrid = colorInts.map { row ->
                            row.map { Color(it) }.toTypedArray()
                        }.toTypedArray()
                    } else {
                        // Grid size changed or is invalid, use today's grid size and clear canvas
                        todayGridSize = currentValidGridSize
                        todayGrid = Array(currentValidGridSize) { Array(currentValidGridSize) { Color.White } }
                    }
                } catch (e: Exception) {
                    // Failed to load, use defaults
                    e.printStackTrace()
                }
            }
        }
    }

    private fun saveTodayCanvasState() {
        val prefs = context?.getSharedPreferences("drawing_state", Context.MODE_PRIVATE) ?: return
        val today = LocalDate.now().toString()

        val canvasData = todayGrid.map { row ->
            row.map { it.toArgb() }.joinToString(",")
        }.joinToString("|")

        prefs.edit()
            .putString("canvas_date", today)
            .putString("canvas_grid_size", todayGridSize.toString())
            .putString("canvas_data", canvasData)
            .apply()
    }

    fun checkAndUpdateDate() {
        val currentDate = LocalDate.now()
        if (todayDate != currentDate) {
            // New day! Reset today's state
            todayDate = currentDate
            todayGridSize = DailyPrompts.getTodaysGridSize()
            todayHasBeenSaved = ArtStorage.hasArtForToday()
            clearTodayCanvas() // This handles both grid reset and undo/redo history clearing
            isInitialized = true
            saveTodayCanvasState() // Save the cleared state
        } else {
            // Same day, just ensure we're initialized
            initializeIfNeeded()
        }
    }

    fun markTodaySaved() {
        todayHasBeenSaved = true
    }

    // Drawing operations with undo/redo support
    fun drawPixelToday(row: Int, col: Int, color: Color) {
        // Bounds check to prevent crashes
        if (row < 0 || row >= todayGridSize || col < 0 || col >= todayGridSize) {
            return
        }
        val oldColor = todayGrid[row][col]
        if (oldColor != color) {
            todayCurrentBatch.add(DrawOperation(row, col, oldColor, color))
            todayGrid = todayGrid.copyOf().apply { this[row][col] = color }
        }
    }

    fun drawPixelFreeMode(row: Int, col: Int, color: Color) {
        // Bounds check to prevent crashes after grid resize
        if (row < 0 || row >= freeDrawGridSize || col < 0 || col >= freeDrawGridSize) {
            return
        }
        val oldColor = freeDrawGrid[row][col]
        if (oldColor != color) {
            freeDrawCurrentBatch.add(DrawOperation(row, col, oldColor, color))
            freeDrawGrid = freeDrawGrid.copyOf().apply { this[row][col] = color }
        }
    }

    fun finishDrawingBatchToday() {
        if (todayCurrentBatch.isNotEmpty()) {
            todayUndoHistory.add(OperationBatch(todayCurrentBatch.toList()))
            todayRedoHistory.clear() // Clear redo history when new operation is made
            // Keep only last 10 operations
            if (todayUndoHistory.size > 10) {
                todayUndoHistory.removeAt(0)
            }
            todayCurrentBatch.clear()
            updateUndoRedoState()
            saveTodayCanvasState() // Save after drawing
        }
    }

    fun finishDrawingBatchFreeMode() {
        if (freeDrawCurrentBatch.isNotEmpty()) {
            freeDrawUndoHistory.add(OperationBatch(freeDrawCurrentBatch.toList()))
            freeDrawRedoHistory.clear() // Clear redo history when new operation is made
            // Keep only last 10 operations
            if (freeDrawUndoHistory.size > 10) {
                freeDrawUndoHistory.removeAt(0)
            }
            freeDrawCurrentBatch.clear()
            updateUndoRedoState()
        }
    }

    // Observable undo/redo state
    var canUndoToday by mutableStateOf(false)
    var canRedoToday by mutableStateOf(false)
    var canUndoFreeMode by mutableStateOf(false)
    var canRedoFreeMode by mutableStateOf(false)

    private fun updateUndoRedoState() {
        canUndoToday = todayUndoHistory.isNotEmpty()
        canRedoToday = todayRedoHistory.isNotEmpty()
        canUndoFreeMode = freeDrawUndoHistory.isNotEmpty()
        canRedoFreeMode = freeDrawRedoHistory.isNotEmpty()
    }

    fun undoToday() {
        if (todayUndoHistory.isNotEmpty()) {
            val batch = todayUndoHistory.removeLastOrNull()
            if (batch != null) {
                // Apply undo operations in reverse order
                val newGrid = todayGrid.map { it.copyOf() }.toTypedArray()
                batch.operations.reversed().forEach { op ->
                    newGrid[op.row][op.col] = op.oldColor
                }
                todayGrid = newGrid
                todayRedoHistory.add(batch)
                updateUndoRedoState()
                saveTodayCanvasState() // Save after undo
            }
        }
    }

    fun redoToday() {
        if (todayRedoHistory.isNotEmpty()) {
            val batch = todayRedoHistory.removeLastOrNull()
            if (batch != null) {
                // Apply redo operations
                val newGrid = todayGrid.map { it.copyOf() }.toTypedArray()
                batch.operations.forEach { op ->
                    newGrid[op.row][op.col] = op.newColor
                }
                todayGrid = newGrid
                todayUndoHistory.add(batch)
                updateUndoRedoState()
                saveTodayCanvasState() // Save after redo
            }
        }
    }

    fun undoFreeMode() {
        if (freeDrawUndoHistory.isNotEmpty()) {
            val batch = freeDrawUndoHistory.removeLastOrNull()
            if (batch != null) {
                // Apply undo operations in reverse order
                val newGrid = freeDrawGrid.map { it.copyOf() }.toTypedArray()
                batch.operations.reversed().forEach { op ->
                    newGrid[op.row][op.col] = op.oldColor
                }
                freeDrawGrid = newGrid
                freeDrawRedoHistory.add(batch)
                updateUndoRedoState()
            }
        }
    }

    fun redoFreeMode() {
        if (freeDrawRedoHistory.isNotEmpty()) {
            val batch = freeDrawRedoHistory.removeLastOrNull()
            if (batch != null) {
                // Apply redo operations
                val newGrid = freeDrawGrid.map { it.copyOf() }.toTypedArray()
                batch.operations.forEach { op ->
                    newGrid[op.row][op.col] = op.newColor
                }
                freeDrawGrid = newGrid
                freeDrawUndoHistory.add(batch)
            }
        }
    }

    fun clearUndoRedoHistoryToday() {
        todayUndoHistory.clear()
        todayRedoHistory.clear()
        todayCurrentBatch.clear()
        updateUndoRedoState()
    }

    fun clearUndoRedoHistoryFreeMode() {
        freeDrawUndoHistory.clear()
        freeDrawRedoHistory.clear()
        freeDrawCurrentBatch.clear()
        updateUndoRedoState()
    }

    fun wouldGridResizeLosePixels(newSize: Int): Boolean {
        if (newSize >= freeDrawGridSize) return false

        val currentSize = freeDrawGridSize
        val offset = (currentSize - newSize) / 2

        // Check if any colored pixels would be lost
        for (row in freeDrawGrid.indices) {
            for (col in freeDrawGrid[row].indices) {
                if (freeDrawGrid[row][col] != Color.White) {
                    // Check if this pixel is outside the new bounds
                    val newRow = row - offset
                    val newCol = col - offset
                    if (newRow < 0 || newRow >= newSize || newCol < 0 || newCol >= newSize) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun resizeFreeDrawGrid(newSize: Int) {
        val oldGrid = freeDrawGrid
        val oldSize = freeDrawGridSize

        freeDrawGridSize = newSize
        val newGrid = Array(newSize) { Array(newSize) { Color.White } }

        if (oldSize > 0) {
            // Calculate offset to center the old content in the new grid
            val offsetRow = (newSize - oldSize) / 2
            val offsetCol = (newSize - oldSize) / 2

            // Copy pixels from old grid to new grid, preserving center
            for (row in oldGrid.indices) {
                for (col in oldGrid[row].indices) {
                    val newRow = row + offsetRow
                    val newCol = col + offsetCol

                    // Only copy if the pixel fits in the new grid
                    if (newRow >= 0 && newRow < newSize && newCol >= 0 && newCol < newSize) {
                        newGrid[newRow][newCol] = oldGrid[row][col]
                    }
                }
            }
        }

        freeDrawGrid = newGrid

        // Adjust cursor position to stay in bounds
        freeDrawCursorX = freeDrawCursorX.coerceIn(0, newSize - 1)
        freeDrawCursorY = freeDrawCursorY.coerceIn(0, newSize - 1)

        // Clear undo/redo history since grid structure changed
        clearUndoRedoHistoryFreeMode()
    }
}
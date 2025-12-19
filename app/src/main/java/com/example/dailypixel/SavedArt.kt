package com.example.dailypixel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class ArtworkType {
    DAILY,
    FREE_DRAW
}

data class SavedPixelArt(
    val id: String,
    val date: LocalDate,
    val prompt: String,
    val paletteName: String,
    val gridSize: Int,
    val pixelData: Array<Array<Color>>,
    val isCompleted: Boolean = true,
    val artworkType: ArtworkType = ArtworkType.DAILY,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val title: String? = null // For free draw custom titles
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SavedPixelArt

        if (id != other.id) return false
        if (date != other.date) return false
        if (prompt != other.prompt) return false
        if (paletteName != other.paletteName) return false
        if (gridSize != other.gridSize) return false
        if (!pixelData.contentDeepEquals(other.pixelData)) return false
        if (isCompleted != other.isCompleted) return false
        if (artworkType != other.artworkType) return false
        if (createdAt != other.createdAt) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + prompt.hashCode()
        result = 31 * result + paletteName.hashCode()
        result = 31 * result + gridSize
        result = 31 * result + pixelData.contentDeepHashCode()
        result = 31 * result + isCompleted.hashCode()
        result = 31 * result + artworkType.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        return result
    }

    fun getDisplayDate(): String {
        return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }
}

// Persistent storage using SharedPreferences
object ArtStorage {
    private var context: Context? = null
    private val savedArt = mutableListOf<SavedPixelArt>()
    private var isLoaded = false

    private val prefs: SharedPreferences?
        get() = context?.getSharedPreferences("daily_pixel_art", Context.MODE_PRIVATE)

    fun initialize(context: Context) {
        this.context = context

        // Check if this is a fresh install by looking at existing data and flag
        val prefs = context.getSharedPreferences("daily_pixel_art", Context.MODE_PRIVATE)
        val isInitialized = prefs.getBoolean("art_storage_initialized", false)
        val hasExistingData = prefs.getInt("art_count", 0) > 0

        if (!isInitialized && !hasExistingData) {
            // True fresh install - no flag and no data
            prefs.edit().clear().apply()
            savedArt.clear()
            prefs.edit().putBoolean("art_storage_initialized", true).apply()
        } else if (!isInitialized && hasExistingData) {
            // Data exists but flag missing - just set flag, don't clear data
            prefs.edit().putBoolean("art_storage_initialized", true).apply()
        }

        if (!isLoaded) {
            loadArtFromStorage()
            isLoaded = true
        }
    }

    private fun loadArtFromStorage() {
        val prefs = this.prefs ?: return
        val artCount = prefs.getInt("art_count", 0)

        for (i in 0 until artCount) {
            try {
                val artData = prefs.getString("art_$i", null) ?: continue
                val art = deserializeArt(artData)
                if (art != null) {
                    savedArt.add(art)
                }
            } catch (e: Exception) {
                // Skip corrupted data
                e.printStackTrace()
            }
        }
    }

    private fun saveArtToStorage() {
        val prefs = this.prefs ?: return
        val editor = prefs.edit()

        // Clear existing data
        editor.clear()

        // Save current art list
        editor.putInt("art_count", savedArt.size)
        savedArt.forEachIndexed { index, art ->
            val artData = serializeArt(art)
            editor.putString("art_$index", artData)
        }

        editor.apply()
    }

    private fun serializeArt(art: SavedPixelArt): String {
        // Convert Color array to Int array for serialization
        val pixelInts = art.pixelData.map { row ->
            row.map { color -> color.toArgb() }.toIntArray()
        }.toTypedArray()

        return listOf(
            art.id,
            art.date.toString(),
            art.prompt,
            art.paletteName,
            art.gridSize.toString(),
            pixelInts.joinToString("|") { row -> row.joinToString(",") },
            art.isCompleted.toString(),
            art.artworkType.name,
            art.createdAt.toString(),
            art.title ?: ""
        ).joinToString(";;;")
    }

    private fun deserializeArt(data: String): SavedPixelArt? {
        return try {
            val parts = data.split(";;;")
            if (parts.size < 10) return null

            val pixelData = parts[5].split("|").map { row ->
                row.split(",").map { colorInt ->
                    Color(colorInt.toInt())
                }.toTypedArray()
            }.toTypedArray()

            SavedPixelArt(
                id = parts[0],
                date = LocalDate.parse(parts[1]),
                prompt = parts[2],
                paletteName = parts[3],
                gridSize = parts[4].toInt(),
                pixelData = pixelData,
                isCompleted = parts[6].toBoolean(),
                artworkType = ArtworkType.valueOf(parts[7]),
                createdAt = LocalDateTime.parse(parts[8]),
                title = parts[9].ifBlank { null }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveArt(art: SavedPixelArt) {
        if (art.artworkType == ArtworkType.DAILY) {
            // For daily art, remove any existing art for the same date
            savedArt.removeAll { it.date == art.date && it.artworkType == ArtworkType.DAILY }
        } else {
            // For free draw, remove if updating existing (same ID)
            savedArt.removeAll { it.id == art.id }
        }
        savedArt.add(art)

        // Persist to storage
        saveArtToStorage()
    }

    fun getAllSavedArt(): List<SavedPixelArt> {
        return savedArt.sortedByDescending { it.createdAt }
    }

    fun getDailyArt(): List<SavedPixelArt> {
        return savedArt.filter { it.artworkType == ArtworkType.DAILY }
            .sortedByDescending { it.date }
    }

    fun getFreeDrawArt(): List<SavedPixelArt> {
        return savedArt.filter { it.artworkType == ArtworkType.FREE_DRAW }
            .sortedByDescending { it.createdAt }
    }

    fun getArtByDate(date: LocalDate): SavedPixelArt? {
        return savedArt.find { it.date == date && it.artworkType == ArtworkType.DAILY }
    }

    fun getArtById(id: String): SavedPixelArt? {
        return savedArt.find { it.id == id }
    }

    fun hasArtForToday(): Boolean {
        return getArtByDate(LocalDate.now()) != null
    }

    fun deleteArt(artId: String): Boolean {
        val removed = savedArt.removeAll { it.id == artId }
        if (removed) {
            saveArtToStorage()
        }
        return removed
    }
}
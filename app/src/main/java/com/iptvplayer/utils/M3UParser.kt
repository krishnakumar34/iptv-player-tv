package com.iptvplayer.utils

import com.iptvplayer.models.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.StringReader

class M3UParser {
    private val client = OkHttpClient()

    suspend fun parsePlaylist(url: String, playlistId: Long): List<Channel> = withContext(Dispatchers.IO) {
        val channels = mutableListOf<Channel>()
        
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val content = response.body?.string() ?: return@withContext channels

            val reader = BufferedReader(StringReader(content))
            var line: String?
            var currentInfo: Map<String, String>? = null
            var position = 0

            while (reader.readLine().also { line = it } != null) {
                line?.let { currentLine ->
                    when {
                        currentLine.startsWith("#EXTINF:") -> {
                            currentInfo = parseExtInf(currentLine)
                        }
                        currentLine.isNotBlank() && !currentLine.startsWith("#") -> {
                            currentInfo?.let { info ->
                                val channel = Channel(
                                    name = info["title"] ?: "Unknown Channel",
                                    url = currentLine.trim(),
                                    logo = info["logo"],
                                    groupTitle = info["group"],
                                    tvgId = info["tvgId"],
                                    tvgName = info["tvgName"],
                                    playlistId = playlistId,
                                    position = position++
                                )
                                channels.add(channel)
                            }
                            currentInfo = null
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext channels
    }

    private fun parseExtInf(line: String): Map<String, String> {
        val info = mutableMapOf<String, String>()
        
        // Extract attributes
        val tvgIdRegex = """tvg-id="([^"]+)"""".toRegex()
        val tvgNameRegex = """tvg-name="([^"]+)"""".toRegex()
        val tvgLogoRegex = """tvg-logo="([^"]+)"""".toRegex()
        val groupTitleRegex = """group-title="([^"]+)"""".toRegex()

        tvgIdRegex.find(line)?.let { info["tvgId"] = it.groupValues[1] }
        tvgNameRegex.find(line)?.let { info["tvgName"] = it.groupValues[1] }
        tvgLogoRegex.find(line)?.let { info["logo"] = it.groupValues[1] }
        groupTitleRegex.find(line)?.let { info["group"] = it.groupValues[1] }

        // Extract title (last part after comma)
        val title = line.substringAfterLast(",").trim()
        if (title.isNotEmpty()) {
            info["title"] = title
        }

        return info
    }
}
